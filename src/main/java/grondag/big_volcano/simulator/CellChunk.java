package grondag.big_volcano.simulator;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import grondag.big_volcano.BigActiveVolcano;
import grondag.big_volcano.Configurator;
import grondag.exotic_matter.simulator.Simulator;
import grondag.exotic_matter.varia.PackedChunkPos;
import net.minecraft.world.chunk.Chunk;
/**
 * Container for all cells in a world chunk.
 * When a chunk is loaded (or updated) all cells that can exist in the chunk are created.
 * 
 * Lifecycle notes
 * ---------------------------------------
 * when a chunk gets lava for the start time
 *       is created
 *       becomes active
 *       retains neighboring chunks
 *       must be loaded
 *       
 * when a chunk gets retained for the start time
 *      is created
 *      must be loaded
 *      
 * chunks can be unloaded when
 *      they are not active
 *      AND they are not retained           
 */
public class CellChunk
{

    public final long packedChunkPos;
    
    public final int xStart;
    public final int zStart;
    
    /**  High x coordinate - INCLUSIVE */
    private final int xEnd;

    /**  High z coordinate - INCLUSIVE */
    private final int zEnd;
    
    /**  unload chunks when they have been unloadable this many ticks */
    private final static int TICK_UNLOAD_THRESHOLD = 200;
    
    /** number of ticks this chunk has been unloadable - unload when reaches threshold */
    private int unloadTickCount = 0;

    /**
     * Simulation tick during which this chunk was last validated.
     * Used to prioritize chunks for validation - older chunks first.
     */
    private long lastValidationTick = 0;
    
    private final LavaCell[] entryCells = new LavaCell[256];

    /** number of cells in the chunk */
    private final AtomicInteger entryCount = new AtomicInteger(0);

    /** Reference to the cells collection in which this chunk lives. */
    public final LavaCells cells;

    /** count of cells in this chunk containing lava */
    private final AtomicInteger activeCount = new AtomicInteger(0);
    
    /** count of cells along the low X edge of this chunk containing lava - used for neighbor loading*/
    private final AtomicInteger activeCountLowX = new AtomicInteger(0);
    
    /** count of cells along the high X edge of this chunk containing lava - used for neighbor loading*/
    private final AtomicInteger activeCountHighX = new AtomicInteger(0);
    
    /** count of cells along the low Z edge of this chunk containing lava - used for neighbor loading*/
    private final AtomicInteger activeCountLowZ = new AtomicInteger(0);
    
    /** count of cells along the high Z edge of this chunk containing lava - used for neighbor loading*/
    private final AtomicInteger activeCountHighZ = new AtomicInteger(0);

    /** count of neighboring active chunks that have requested this chunk to remain loaded*/
    private final AtomicInteger retainCount = new AtomicInteger(0);

    /** count of cells that have requested validation since last validation occurred */
    private final AtomicInteger validationCount = new AtomicInteger(0);

    //    /** Set to true after start loaded. Also set true by NBTLoad.  */
//    private boolean isLoaded = false;

    /** Set to true when chunk is unloaded and should no longer be processed */
    private boolean isUnloaded = false;

    /** If true, chunk needs full validation. Should always be true if isLoaded = False. */
    private boolean needsFullValidation = true;

    CellChunk(long packedChunkPos, LavaCells cells)
    {
        this.packedChunkPos = packedChunkPos;
        this.xStart = PackedChunkPos.getChunkXStart(packedChunkPos);
        this.zStart = PackedChunkPos.getChunkZStart(packedChunkPos);
        this.xEnd = this.xStart + 15;
        this.zEnd = this.zStart + 15;
        
        this.cells = cells;
        
        if(Configurator.VOLCANO.enableLavaChunkBufferTrace)
            BigActiveVolcano.INSTANCE.info("Created chunk buffer with corner x=%d, z=%d", this.xStart, this.zStart);
    }

//    /** for use when reading from NBT */
//    public void setLoaded()
//    {
//        this.isLoaded = true;
//    }
    
    public boolean isUnloaded()
    {
        return this.isUnloaded;
    }
    
    /** 
     * Marks this chunk for full validation.  
     * Has no effect if it already so or if chunk is unloaded.
     */
    public void requestFullValidation()
    {
        if(!this.isUnloaded) this.needsFullValidation = true;
    }
    
    /**
     * True if chunk needs to be loaded for start time or a full revalidation has been requested.
     * Will also be true if more than 1/4 of the cells in the chunk are individually marked for validation.
     */
    public boolean needsFullLoadOrValidation()
    {
        return (this.needsFullValidation || this.validationCount.get() > 64) && !this.isUnloaded;
    }
    
    public int validationPriority()
    {
        if(this.isUnloaded) return -1;
        
        if(this.isNew()) return Integer.MAX_VALUE;
        
        if(this.needsFullValidation) return 256;
        
        return this.validationCount.get();
    }
    
    public boolean isNew()
    {
        return this.lastValidationTick == 0;
    }
    
    /**
     * Tick during which this chunk was last validated, or zero if has never been validated.
     */
    public long lastValidationTick()
    {
        return this.lastValidationTick;
    }
    
    /**
     * Validates any cells that have been marked for individual validation.
     * 
     * Will return without doing any validation if a full validation is already needed.
     * @param worldBuffer
     * 
     * @return true if any cells were validated.
     */
    public boolean validateMarkedCells()
    {
        this.lastValidationTick = Simulator.currentTick();
        
        if(this.isUnloaded || this.needsFullLoadOrValidation() || this.validationCount.get() == 0) return false;

        if(Configurator.VOLCANO.enableLavaChunkBufferTrace)
            BigActiveVolcano.INSTANCE.info("Validating marked cells in chunk with corner x=%d, z=%d", this.xStart, this.zStart);
        
        Chunk chunk = this.cells.sim.world.getChunkFromChunkCoords(PackedChunkPos.getChunkXPos(this.packedChunkPos), PackedChunkPos.getChunkZPos(this.packedChunkPos));
        
        CellStackBuilder builder = new CellStackBuilder();

        synchronized(this)
        {
            
            for(int x = 0; x < 16; x++)
            {
                for(int z = 0; z < 16; z++)
                {
                    LavaCell entryCell = this.getEntryCell(x, z);

                    if(entryCell != null && entryCell.isValidationNeeded())
                    {
                        entryCell = builder.updateCellStack(cells, chunk, entryCell, this.xStart + x, this.zStart + z);
                        entryCell.setValidationNeeded(false);
                        this.setEntryCell(x, z, entryCell);
                    }
                }
            }
            this.validationCount.set(0);
            
        }
        
        this.forEach(cell -> cell.updateRawRetentionIfNeeded());
        
        return true;
    }

    /**
     * Creates cells for the given chunk if it is not already loaded.
     * If chunk is already loaded, validates against the chunk data provided.
     */
    public void loadOrValidateChunk()
    {
        synchronized(this)
        {
            if(this.isUnloaded) return;

            if(Configurator.VOLCANO.enableLavaChunkBufferTrace)
                BigActiveVolcano.INSTANCE.info("Loading (or reloading) chunk buffer with corner x=%d, z=%d", this.xStart, this.zStart);
            
            CellStackBuilder builder = new CellStackBuilder();
            
            Chunk chunk = this.cells.sim.world.getChunkFromChunkCoords(PackedChunkPos.getChunkXPos(this.packedChunkPos), PackedChunkPos.getChunkZPos(this.packedChunkPos));

            for(int x = 0; x < 16; x++)
            {
                for(int z = 0; z < 16; z++)
                {
                    LavaCell entryCell = this.getEntryCell(x, z);

                    if(entryCell == null)
                    {
                        this.setEntryCell(x, z, builder.buildNewCellStack(this.cells, chunk, this.xStart + x, this.zStart + z));
                    }
                    else
                    {
                        this.setEntryCell(x, z, builder.updateCellStack(this.cells, chunk, entryCell, this.xStart + x, this.zStart + z));
                    }
                }
            }

            //  this.isLoaded = true;
            this.needsFullValidation = false;
            this.validationCount.set(0);
            this.lastValidationTick = Simulator.currentTick();
        }
        
        this.forEach(cell -> cell.updateRawRetentionIfNeeded());
    }

    /**
     * Call from any cell column when the start cell in that column
     * is marked for validation after the last validation of that column.
     */
    public void incrementValidationCount()
    {
        if(this.isUnloaded) return;

        this.validationCount.incrementAndGet();
    }
    
    public int getActiveCount()
    {
        return this.activeCount.get();
    }
    
    /**
     * Call when any cell in this chunk becomes active.
     * The chunk must already exist at this point but will force it to be and stay loaded.
     * Will also cause neighboring chunks to be loaded so that lava can flow into them.
     */
    public void incrementActiveCount(int blockX, int blockZ)
    {
        if(this.isUnloaded) return;

        this.activeCount.incrementAndGet();
        
       // create (if needed) and retain neighbors if newly have lava at the edge of this chunk
        if(blockX == this.xStart)
        {
            if(this.activeCountLowX.incrementAndGet() == 1) 
                this.cells.getOrCreateCellChunk(this.xStart - 16, this.zStart).retain();
        }
        else if(blockX == this.xEnd)
        {
            if(this.activeCountHighX.incrementAndGet() == 1) 
                this.cells.getOrCreateCellChunk(this.xStart + 16, this.zStart).retain();
        }
        else if(blockZ == this.zStart)
        {
            if(this.activeCountLowZ.incrementAndGet() == 1) 
                this.cells.getOrCreateCellChunk(this.xStart, this.zStart - 16).retain();
        }
        else if(blockZ == this.zEnd)
        {
            if(this.activeCountHighZ.incrementAndGet() == 1) 
                this.cells.getOrCreateCellChunk(this.xStart, this.zStart + 16).retain();
        }
            
    }

    /**
     * Call when any cell in this chunk becomes inactive.
     * When no more cells are active will allow this and neighboring chunks to be unloaded.
     */
    public void decrementActiveCount(int blockX, int blockZ)
    {
        if(this.isUnloaded) return;
        
        // release neighbors if no longer have lava at the edge of this chunk
        if(blockX == this.xStart)
        {
            if(this.activeCountLowX.decrementAndGet() == 0) 
                this.releaseChunkIfExists(this.xStart - 16, this.zStart);
        }
        else if(blockX == this.xEnd)
        {
            if(this.activeCountHighX.decrementAndGet() == 0) 
                this.releaseChunkIfExists(this.xStart + 16, this.zStart);
        }
        else if(blockZ == this.zStart)
        {
            if(this.activeCountLowZ.decrementAndGet() == 0) 
                this.releaseChunkIfExists(this.xStart, this.zStart - 16);
        }
        else if(blockZ == this.zEnd)
        {
            if(this.activeCountHighZ.decrementAndGet() == 0) 
                this.releaseChunkIfExists(this.xStart, this.zStart + 16);
        }
        if(this.activeCount.decrementAndGet() == 0)
        {
        }
    }

    private void releaseChunkIfExists(int blockX, int blockZ)
    {
        CellChunk chunk = this.cells.getCellChunk(blockX, blockZ);
        if(chunk == null)
        {
            assert false : "Neighboring cell chunk not found during release - expected it to be loaded.";
        }
        else
        {
            chunk.release();
        }
    }
    
    /**
     * Call when a neighboring chunk becomes active (has active cells) to force this
     * chunk to be and stay loaded. (Getting a reference to this chunk to call retain() will cause it to be created.)
     * This creates connections and enables lava to flow into this chunk if it should.
     */
    public void retain()
    {
        if(!this.isUnloaded) this.retainCount.incrementAndGet();
    }

    /**
     * Call when a neighboring chunk no longer has active cells. 
     * Allow this chunk to be unloaded if no other neighbors are retaining it and it has no active cells.
     */
    public void release()
    {
        if(!this.isUnloaded) this.retainCount.decrementAndGet();
    }

    /**
     * Returns true if chunk should be unloaded. Call once per tick 
     */
    public boolean canUnload()
    {
//        HardScience.log.info("chunk " + this.xStart + ", " + this.zStart + " activeCount=" + this.activeCount.get() 
//        + "  retainCount=" + this.retainCount.get() + " unloadTickCount=" + this.unloadTickCount);
        
        if(this.isUnloaded || this.isNew()) return false;
        
        //  complete validation before unloading because may be new info that could cause chunk to remain loaded
        if(this.activeCount.get() == 0 && this.retainCount.get() == 0 && this.validationCount.get() == 0)
        {
            return this.unloadTickCount++ >= TICK_UNLOAD_THRESHOLD;
        }
        else
        {
            this.unloadTickCount = 0;
            return false;
        }
    }

    public void unload()
    {
        if(this.isUnloaded) return;
        
        if(Configurator.VOLCANO.enableLavaChunkBufferTrace)
            BigActiveVolcano.INSTANCE.info("Unloading chunk buffer with corner x=%d, z=%d", this.xStart, this.zStart);

        for(int x = 0; x < 16; x++)
        {
            for(int z = 0; z < 16; z++)
            {
                LavaCell entryCell = this.getEntryCell(x, z);

                if(entryCell == null)
                {
                    BigActiveVolcano.INSTANCE.warn("Null entry cell in chunk being unloaded"); 
                    //assert false : "Null entry cell in chunk being unloaded.";
                    continue;
                }
                
                LavaCell firstCell = entryCell.firstCell();
                if(firstCell == null)
                {
                    assert false: "First cell in entry cell is null in chunk being unloaded.";
                    
                    // strange case - do our best
                    entryCell.setDeleted();
                    this.setEntryCell(x, z, null);
                    continue;
                }
                entryCell = firstCell;
                
                assert entryCell.belowCell() == null : "First cell is not actually the start cell.";
                    
                do
                {
                    LavaCell nextCell = entryCell.aboveCell();
                    entryCell.setDeleted();
                    entryCell = nextCell;
                }
                while(entryCell != null);

                this.setEntryCell(x, z, null);
            }
        }
        
        this.isUnloaded = true;
        
        
    }

    /** 
     * Returns the starting cell for the stack of cells located at x, z.
     * Returns null if no cells exist at that location.
     * Thread safe.
     */
    @Nullable LavaCell getEntryCell(int x, int z)
    {
        assert !this.isUnloaded : "derp in CellChunk unloading - returning cell from unloaded chunk in getEntryCell";
        
        LavaCell result = this.entryCells[getIndex(x, z)];
        assert result == null || !result.isDeleted() : "derp in CellChunk unloading - returning deleted cell from getEntryCell";
        return result;
    }

    /**
     * Sets the entry cell for the stack of cells located at x, z.
     * Should be thread safe if not accessing same x, z.
     */
    void setEntryCell(int x, int z, @Nullable LavaCell entryCell)
    {
        if(this.isUnloaded) return;

        int i = getIndex(x, z);
        boolean wasNull = this.entryCells[i] == null;

        this.entryCells[i] = entryCell;

        if(wasNull)
        {
            if(entryCell != null) this.entryCount.incrementAndGet();
        }
        else
        {
            if(entryCell == null) this.entryCount.decrementAndGet();
        }
    }

    /** How many x. z locations in this chunk have at least one cell? */
    public int getEntryCount()
    {
        return this.entryCount.get();
    }

    private static int getIndex(int x, int z)
    {
        return ((x & 15) << 4) | (z & 15);
    }

    public boolean isDeleted()
    {
        return this.isUnloaded;
    }

    public void provideBlockUpdatesAndDoCooling()
    {
        final LavaSimulator sim = this.cells.sim;
        
        final int tick = Simulator.currentTick();
        
        CellChunk c = sim.cells.getCellChunk(this.xStart - 16, this.zStart);
        final boolean isLoadedLowX = c != null && !c.isNew();
        
        c = sim.cells.getCellChunk(this.xStart + 16, this.zStart);
        final boolean isLoadedHighX = c != null && !c.isNew();
        
        c = sim.cells.getCellChunk(this.xStart , this.zStart - 16);
        final boolean isLoadedLowZ = c != null && !c.isNew();
        
        c = sim.cells.getCellChunk(this.xStart , this.zStart + 16);
        final boolean isLoadedHighZ = c != null && !c.isNew();
        
        for(LavaCell entryCell : this.entryCells)
        {
            // don't allow cooling on edge cells if neighbor chunk not loaded yet
            // because won't have connection formed yet
            if(entryCell.x() == this.xStart && !isLoadedLowX) continue;
            if(entryCell.x() == this.xEnd && !isLoadedHighX) continue;
            if(entryCell.z() == this.zStart && !isLoadedLowZ) continue;
            if(entryCell.z() == this.zEnd && !isLoadedHighZ) continue;
            
            LavaCell cell = entryCell.firstCell();
            
            while(cell != null)
            {
                cell.provideBlockUpdateIfNeeded(sim);
                
                // necessary because cell may be deleted by cooling
                // and would no longer have a reference to the next cell
                LavaCell nextCell = cell.aboveCell();
                
                if(cell.canCool(tick))
                {
                    sim.coolCell(cell);
                }
                cell = nextCell;
            }
        }
        
        this.forEach(cell -> cell.updateRawRetentionIfNeeded());
    }

    /**
     * Applies the given operation to all cells in the chunk.<p>
     * 
     * Do not use for operations that may add or remove cells.
     */
    public void forEach(Consumer<LavaCell> consumer)
    {
        for(int i = 0; i < 256; i++)
        {
            LavaCell c = this.entryCells[i];
            if(c == null) continue;
            c = c.firstCell();
            
            do
            {
                consumer.accept(c);
                c = c.aboveCell();
            } while(c != null);
        }
    }

}