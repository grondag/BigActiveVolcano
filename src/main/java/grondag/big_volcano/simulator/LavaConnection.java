package grondag.big_volcano.simulator;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import grondag.big_volcano.Configurator;

public class LavaConnection
{
   
    public static final Predicate<LavaConnection> REMOVAL_PREDICATE = new Predicate<LavaConnection>()
    {
        @Override
        public boolean test(@Nullable LavaConnection t)
        {
            return t == null || t.isDeleted;
        }
    };
    
    /** by convention, start cell will have the lower-valued id */
    public final LavaCell firstCell;
    
    /** by convention, second cell will have the higher-valued id */
    public final LavaCell secondCell;
    
    /**
     * Direction of flow in the current tick, if any.
     * Set by {@link #setupTick(LavaCell)} at the start of each tick.
     */
    private FlowDirection direction = FlowDirection.NONE;
    
    /**
     * True if updated the last tick for cells in this connection because we
     * had a flow this tick.  If false, has been no flow, or we haven't updated yet.
     * Set to false during {@link #doFirstStep()} unless there was a flow on the first step.
     * Subsequent calls to {@link #doStep()} will set to true if there is a flow
     * (and will also update the cell ticks.)
     */
    private boolean didUpdateCellTicks = false;
    
    
    /**
     * True if this connection has been marked for removal.
     * Connection should not be processed or considered valid if true.
     */
    private boolean isDeleted = false;

    /**
     * Used in cell-wise connection processing.  The drop from one 
     * fluid surface to the other, in units.  Capped at 2 blocks of drop
     * because target surface shouldn't influence that far.
     * 
     * Set during {@link #setupTick(LavaCell)}.
     * 
     * TODO: encapsulate if keeping
     * 
     */
    public int drop;
    
    /**
     * Fluid units that can low through this connection during a single step.
     * Is simply 1/4 of the initial value of {@link #flowRemainingThisTick} at the start of the tick.
     * Necessary so that fluid has a chance to flow in more than one direction. 
     * If we did not limit flow per step, then flow would usually always go across a single
     * connection, even if the vertical drop is the same.
     * 
     * TODO: encapsulate if need to reference externally
     */
    public int maxFlowPerStep;
    
    /**
     * Established during tick setup - the next connection to flow after this one.
     */
    public @Nullable LavaConnection nextToFlow;
    
    /**
     * If {@link #nextToFlow} is non-null and this is true, 
     * next connection should wait until next round to be processed.
     */
    public boolean isEndOfRound = false;
    
    /**
     * Floor of the "from" cell, in fluid units. 
     * Maintained during {@link #setupTick()}.
     */
    private int floorUnitsFrom;
    
    /**
     * Floor of the "to" cell, in fluid units. 
     * Maintained during {@link #setupTick()}.
     */
    private int floorUnitsTo;
    
    /**
     * Total volume of the "from" cell, in fluid units. 
     * Maintained during {@link #setupTick()}.
     */
    private int volumeUnitsFrom;
    
    /**
     * Total volume of the "to" cell, in fluid units. 
     * Maintained during {@link #setupTick()}.
     */
    private int volumeUnitsTo;
    
    /** 
     * True if ceiling of "to" cell is lower than ceiling of "from" cell. 
     * Only valid if {@link #isFlowEnabled} is true.
     * Maintained during {@link #setupTick()} 
     */
    private boolean isToLowerThanFrom;
    
    /** 
     * When total fluid in both cells is above this amount, 
     * both cells will be under pressure at equilibrium.
     * Maintained via {@link #setPressureThresholds()} during {@link #setupTick()}. 
     */
    private int dualPressureThreshold;
    
    /** 
     * When total fluid in both cells is above this amount, 
     * at least one cell will be under pressure at equilibrium.
     * Maintained via {@link #setPressureThresholds()} during {@link #setupTick()}. 
     */
    private int singlePressureThreshold;
    
    //TODO: can probably be normal int because will alwoays be updated from same thread?
    public static AtomicInteger totalFlow = new AtomicInteger(0);

    public LavaConnection(LavaCell firstCell, LavaCell secondCell)
    {
        this.firstCell = firstCell;
        this.secondCell = secondCell;
        firstCell.addConnection(this);
        secondCell.addConnection(this);
    }
    
    public LavaCell getOther(LavaCell cellIAlreadyHave)
    {
        return cellIAlreadyHave == this.firstCell ? this.secondCell : this.firstCell;
    }
    
    private void setPressureThresholds()
    {
        int ceilFrom = this.floorUnitsFrom + this.volumeUnitsFrom;
        int ceilTo = this.floorUnitsTo + this.volumeUnitsTo;
        
        if(ceilFrom > ceilTo)
        {
            this.isToLowerThanFrom = true;
            this.dualPressureThreshold = AbstractLavaCell.dualPressureThreshold(this.floorUnitsFrom, this.volumeUnitsFrom, this.floorUnitsTo, this.volumeUnitsTo);
            this.singlePressureThreshold = AbstractLavaCell.singlePressureThreshold(this.floorUnitsFrom, this.floorUnitsTo, ceilTo);
        }
        else
        {
            this.isToLowerThanFrom = false;
            this.dualPressureThreshold = AbstractLavaCell.dualPressureThreshold(this.floorUnitsTo, this.volumeUnitsTo, this.floorUnitsFrom, this.volumeUnitsFrom);
            this.singlePressureThreshold = AbstractLavaCell.singlePressureThreshold(this.floorUnitsFrom, this.floorUnitsTo, ceilFrom);
        }
        
    }
    
    
    /**
     * Returns true if connection should be allowed to flow from high to low.
     * Also updates {@link #maxFlowPerStep}
     */
    private boolean setFlowLimitsThisTick(LavaCell cellHigh, LavaCell cellLow, int surfaceHigh, int surfaceLow)
    {
        int diff = surfaceHigh - surfaceLow;
        if(diff < 2 || cellHigh.isEmpty())
        {
            //not enough lava to flow
            return false;
        }
     
        // want to flow faster if under pressure - so use surface of high cell if above low cell ceiling
        // and if flowing into an open area use the max height of the low cell
        int flowWindow = Math.max(surfaceHigh, cellLow.ceilingUnits()) - Math.max(cellHigh.floorUnits(), cellLow.floorUnits());
        
        if(flowWindow < LavaSimulator.FLUID_UNITS_PER_LEVEL)
        {
            //cross-section too small
            return false;
        }
        
        this.maxFlowPerStep = Math.min(flowWindow / LavaConnections.STEP_COUNT, cellHigh.maxOutputPerStep);
        return true;
    }
    
    /**
     *  Does a step and if there was a flow on this connection 
     *  will update the tick index of both cells.  Also is a hook for
     *  any internal setup or accounting the connection needs to do at
     *  the start of each tick.
     */
    public void doFirstStep()
    {
        if(this.doStepWork() != 0)
        {
            this.firstCell.updateLastFlowTick();
            this.secondCell.updateLastFlowTick();
            this.didUpdateCellTicks = true;
        }
        else this.didUpdateCellTicks = false;
    }
    
    /**
     * Like {@link #doFirstStep()} but doesn't do per-tick setup or accounting.
     */
    public void doStep()
    {
        if(this.doStepWork() != 0 && !this.didUpdateCellTicks)
        {
            this.firstCell.updateLastFlowTick();
            this.secondCell.updateLastFlowTick();
            this.didUpdateCellTicks = true;
        }
    }
    
    /**
     * Returns the From cell based on current flow direction.
     * Returns null if no flow direction.
     * @return
     */
    public @Nullable LavaCell getFromCell()
    {
        return this.direction.fromCell(this);
    }
    
    /**
     * Guts of doStep.  Returns amount that flowed.
     */
    private int doStepWork()
    {
        if(this.isDeleted) return 0;

        switch(this.direction)
        {
        case ONE_TO_TWO:
            return this.tryFlow(this.firstCell, this.secondCell);

        case TWO_TO_ONE:
            return this.tryFlow(this.secondCell, this.firstCell);

        case NONE:
        default:
            return 0;
        
        }
    }
    
    /** 
     * Returns amount that should flow from "from" cell to "to" cell to equalize pressure.
     * 
     * Definitions  
     *      c   Pressure Factor (constant)
     *      t   total lava (invariant)
     *      Pa Pb   pressurized fluid surface
     *      Sa  Sb  normal fluid surface (bounded by cell ceiling)
     *      Fa Fb   column floor (fixed)
     *      Ua Ub   normal fluid units (bounded by cell volume)
     *      Xa Xb   extra (pressure) fluid units
     *      
     *      
     *      Sa = Fa + Ua, Sb = Fb + Ub  compute normal surfaces
     *      Pa = Sa + cXa, Pb = Sb + cXb    compute pressure surffaces
     *      Pa = Fa + Ua + cXa, Pb = Fb + Ub + cXb  expand pressure surface forumla
     *      
     *  1   t = Ua + Ub + Xa + Xb   conservation of fluid
     *  2   Pa = Pb equalize pressurized fluid surface
     *  3   Fa + Ua + cXa = Fb + Ub + cXb   expanded equalization formula
     *          
     *      Single Column Under Pressure    
     *  4   t = Ua + Ub + Xb    If b has pressure (Ub is fixed and Xa=0)
     *  5   Fa + Ua = Fb + Ub + cXb     
     *  6   Ua = Fb + Ub + cXb - Fa rearrange #5
     *  7   t = Fb + Ub + cXb - Fa + Ub + Xb    substitue #6 into #4
     *  8   t = Fb - Fa + 2Ub + (c+1)Xb simplify
     *  9   Xb = (t + Fa - Fb - 2Ub)/(c+1)  solve for Xb, then use #6 to obtain Ua
     */
    private int singlePressureFlow(int fluidTo, int fluidFrom, int fluidTotal)
    {
        // Single pressure flow is not symmetrical.
        // Formula assumes that the lower cell is full at equilibrium.
        // "Lower" means lowest ceiling.
        
        int newFluidFrom;
        
        if(this.isToLowerThanFrom)
        {
            // flowing from upper cell into lower, creating pressure in lower cell
            // "to" cell corresponds to subscript "b" in formula.
            final int pressureUnitsLow = (fluidTotal + this.floorUnitsFrom - this.floorUnitsTo - 2 * this.volumeUnitsTo) / AbstractLavaCell.PRESSURE_FACTOR_PLUS;
          
            newFluidFrom = fluidTotal - this.volumeUnitsTo - pressureUnitsLow;
        }
        else
        {
            // "from" cell corresponds to subscript "b" in formula.
            // flowing from lower cell into upper, relieving pressure in lower cell
            
            // adding pressure factor to numerator so that we round up the result without invoking floating point math
            // Rounding up so that we don't allow the new pressure surface of "from" cell to be lower than the "to" cell.
            final int pressureUnitsLow = (fluidTotal + this.floorUnitsTo - this.floorUnitsFrom - 2 * this.volumeUnitsFrom + AbstractLavaCell.PRESSURE_FACTOR) / AbstractLavaCell.PRESSURE_FACTOR_PLUS;
            
            newFluidFrom = this.volumeUnitsFrom + pressureUnitsLow;
            
        }
        
        return fluidFrom - newFluidFrom;

    }
    
    /** 
     * Returns amount that should flow from "from" cell to "to" cell to equalize pressure.
     * 
     * Definitions  
     *      c   Pressure Factor (constant)
     *      t   total lava (invariant)
     *      Pa Pb   pressurized fluid surface
     *      Sa  Sb  normal fluid surface (bounded by cell ceiling)
     *      Fa Fb   column floor (fixed)
     *      Ua Ub   normal fluid units (bounded by cell volume)
     *      Xa Xb   extra (pressure) fluid units
     *      
     *      
     *      1    t = Ua + Ub + Xa + Xb   conservation of fluid, Ua and Ub are fixed
     *      2   Pa = Pb equalize pressurized fluid surface
     *      3   Fa + Ua + cXa = Fb + Ub + cXb   expanded equalization formula
     *      
     *              
     *      6   Xb = t - Ua - Ub - Xa   rearrange #1
     *      7   cXa = Fb + Ub - Fa - Ua +cXb    rearrange #3
     *      8   cXa = Fb + Ub - Fa - Ua +c(t - Ua - Ub - Xa)    substitute #6 into #3
     *      9   cXa = Fb + Ub - Fa - Ua +ct - cUa - cUb - cXa   
     *      10  2cXa = Fb - Fa + Ub - cUb - Ua - cUa +ct    
     *      11  2cXa = Fb - Fa + (1-c)Ub - (c+1)Ua + ct 
     *      12  Xa = (Fb - Fa + (1-c)Ub - (c+1)Ua + ct) / 2c    solve for Xa, then use #6 to obtain Xb
     */
    private int dualPressureFlow(int fluidTo, int fluidFrom, int fluidTotal)
    {        
        // Does not matter which cell has higher ceiling when both are under pressure. 
        // Assigning "from" cell to subscript a in formula.
        
        int fromPressureUnits = (this.floorUnitsTo - this.floorUnitsFrom 
                + (1 - AbstractLavaCell.PRESSURE_FACTOR) * this.volumeUnitsTo
                - AbstractLavaCell.PRESSURE_FACTOR_PLUS * this.volumeUnitsFrom
                + AbstractLavaCell.PRESSURE_FACTOR * fluidTotal

                // Adding PRESSURE_FACTOR to numerator term rounds up without floating point math
                // This ensure "from cell" does not flow so much that its's effective surface is below the "to cell."
                // If this happened it could lead to oscillation that would prevent cell cooling and waste CPU.
                + AbstractLavaCell.PRESSURE_FACTOR) / AbstractLavaCell.PRESSURE_FACTOR_X2;
        
        return fluidFrom - this.volumeUnitsFrom - fromPressureUnits;
    }
    
    /** 
     * Returns amount that should flow from "from" cell to "to" cell to equalize level in absence of pressure in either cell.
     * 
     * Definitions  
     *      t   total lava (invariant)
     *      Fa Fb   column floor (fixed)
     *      Ua Ub   normal fluid units (bounded by cell volume)
     *      
     *      1    t = Ua + Ub            conservation of fluid
     *      2   Fa + Ua = Fb + Ub       equalization condition
     *      
     *              
     *      3   Ub = t - Ua             rearrange #1
     *      4   Ua = Fb - Ub - Fa       rearrange #2
     *      
     *      5   Ua = Fb + t - Ua - Fa   substitute #3 into #4
     *      
     *      5   2Ua = Fb - Fa + t
     *      6   Ua = (Fb - Fa + t) / 2
     */
    private int freeFlow(int fluidTo, int fluidFrom, int fluidTotal)
    {        
        // Assigning "from" cell to subscript a in formula.
        // Adding 1 to round up without floating point math
        // This ensure "from" cell does not flow to level below "to" cell.
        return fluidFrom - (this.floorUnitsTo - this.floorUnitsFrom + fluidTotal + 1) / 2;
    }
    
    /** 
     * Returns absolute value of units that flowed, if any.
     * Also updates per-tick tracking total for from cell.
     */
    private int tryFlow(LavaCell cellFrom, LavaCell cellTo)
    {
        final int availableFluidUnits = Math.min(this.maxFlowPerStep, cellFrom.getAvailableFluidUnits());
        if(availableFluidUnits < LavaSimulator.MIN_FLOW_UNITS) return 0;
        
        final int fluidFrom = cellFrom.fluidUnits();
        final int fluidTo = cellTo.fluidUnits();
        final int surfaceTo = AbstractLavaCell.pressureSurface(this.floorUnitsTo, this.volumeUnitsTo, fluidTo);
        final int surfaceFrom = AbstractLavaCell.pressureSurface(this.floorUnitsFrom, this.volumeUnitsFrom, fluidFrom);
        if(surfaceFrom > surfaceTo)
        {
            final int fluidTotal = fluidTo + fluidFrom;
            int flow;
            
            if(fluidTotal > this.dualPressureThreshold)
            {
                flow = Math.min(availableFluidUnits, this.dualPressureFlow(fluidTo, fluidFrom, fluidTotal));
            }
            else if(fluidTotal > this.singlePressureThreshold)
            {
                flow = Math.min(availableFluidUnits, this.singlePressureFlow(fluidTo, fluidFrom, fluidTotal));
            }
            else
            {
                flow = Math.min(availableFluidUnits, this.freeFlow(fluidTo, fluidFrom, fluidTotal));
            }

            if(flow < LavaSimulator.MIN_FLOW_UNITS)
            {
                return 0;
            }
            else 
            {
                if(cellFrom.changeFluidUnitsIfMatches(-flow, fluidFrom))
                {
                    if(cellTo.changeFluidUnitsIfMatches(flow, fluidTo))
                    {                        
                        if(Configurator.VOLCANO.enableFlowTracking) totalFlow.addAndGet(flow);
                        cellFrom.flowThisTick.addAndGet(flow);
                        return flow;
                    }
                    else
                    {
                        //undo start change if second isn't successful
                        cellFrom.changeFluidUnits(flow);
                        return 0;
                    }
                }
                else
                {
                    return 0;
                }
            }
        }
        else
        {
            return 0;
        }
    }
    
    /** marks connection deleted. Does not release cells */
    public void setDeleted()
    {
        this.isDeleted = true;
    }

    public boolean isDeleted()
    {
        return this.isDeleted;
    }
    
    public boolean isValid()
    {
        return !this.isDeleted && this.firstCell.canConnectWith(this.secondCell);
    }
    
    /** true if either cell has fluid */
    public boolean isActive()
    {
        return this.firstCell.fluidUnits() > 0 || this.secondCell.fluidUnits() > 0;
    }
    
    /** 
     * Represents potential energy of flowing across this connection based on underlying terrain
     * and irrespective of fluid contents. Higher number means higher potential and should be 
     * given a higher priority for flow.  Zero means no potential / can't flow.
     */
    public int getTerrainDrop()
    {
        return Math.abs(this.firstCell.floorLevel() - this.secondCell.floorLevel());
    }
    
    /**
     * Absolute difference in fluid surface levels. Used for connection sorting.
     */
    public int getSurfaceDrop()
    {
        return Math.abs(this.firstCell.pressureSurfaceLevel() - this.secondCell.pressureSurfaceLevel());
    }
    
    /**
     * Determine if connection can flow, and if so, in which direction.
     * If flowed last tick, then direction cannot reverse - must start go to none and then to opposite direction.
     * returns true if can flow from the calling cell.
     */
    public boolean setupTick(LavaCell sourceCell)
    {
        if(this.isDeleted) return false;
        
        int surface1 = this.firstCell.pressureSurfaceUnits();
        int surface2 = this.secondCell.pressureSurfaceUnits();
        
        if(surface1 == surface2)
        {
            this.direction = FlowDirection.NONE;
            return false;
        }
        
        if(surface1 > surface2)
        {
            // Will be set up from other cell
            if(this.secondCell == sourceCell) return false; 
            
            // don't allow switch of direction in same tick
            if(this.direction == FlowDirection.TWO_TO_ONE) 
            {
                this.direction = FlowDirection.NONE;
                return false;
            }
            
            if(this.setFlowLimitsThisTick(this.firstCell, this.secondCell, surface1, surface2))
            {
                final int floorUnits1 = this.firstCell.floorUnits();
                final int volumeUnits1 = this.firstCell.ceilingUnits() - floorUnits1;
                final int floorUnits2 = this.secondCell.floorUnits();
                final int volumeUnits2 = this.secondCell.ceilingUnits() - floorUnits2;
                
                this.floorUnitsFrom = floorUnits1;
                this.floorUnitsTo = floorUnits2;
                this.volumeUnitsFrom = volumeUnits1;
                this.volumeUnitsTo = volumeUnits2;
                this.setPressureThresholds();
                
                // for sorting
                this.drop = Math.min(floorUnits1 - floorUnits2, LavaSimulator.FLUID_UNITS_PER_TWO_BLOCKS);
                this.direction = FlowDirection.ONE_TO_TWO;
                return true;
            }
            else
            {
                this.direction = FlowDirection.NONE;
                return false;
            }
        }
        else // surface1 < surface2
        {
            
            // Will be set up from other cell
            if(this.firstCell == sourceCell) return false; 
            
            // don't allow switch of direction in same tick
            if(this.direction == FlowDirection.ONE_TO_TWO) 
            {
                this.direction = FlowDirection.NONE;
                return false;
            }

            if(this.setFlowLimitsThisTick(this.secondCell, this.firstCell, surface2, surface1))
            {
                final int floorUnits1 = this.firstCell.floorUnits();
                final int volumeUnits1 = this.firstCell.ceilingUnits() - floorUnits1;
                final int floorUnits2 = this.secondCell.floorUnits();
                final int volumeUnits2 = this.secondCell.ceilingUnits() - floorUnits2;
                
                this.floorUnitsFrom = floorUnits2;
                this.floorUnitsTo = floorUnits1;
                this.volumeUnitsFrom = volumeUnits2;
                this.volumeUnitsTo = volumeUnits1;
                this.setPressureThresholds();
                
                // for sorting
                this.drop = Math.min(floorUnits2 - floorUnits1, LavaSimulator.FLUID_UNITS_PER_TWO_BLOCKS);
                this.direction = FlowDirection.TWO_TO_ONE;
                return true;
            }
            else
            {
                this.direction = FlowDirection.NONE;
                return false;
            }
        }
    }     
}