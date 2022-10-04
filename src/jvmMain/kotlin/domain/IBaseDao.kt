package domain

interface IBaseDao<ENTITY : IEntity> {
    /**
     * Get single Entity by [id]
     */
    suspend fun getByID(id: String): ENTITY?

    /**
     * Insert Entity
     */
    suspend fun insert(entity: ENTITY)

    /**
     * Update Entity
     */
    suspend fun update(entity: ENTITY)


    /**
     * Update Entities (batch)
     */
    suspend fun update(entities: List<ENTITY>)


    /**
     * Remove Entity by [id]
     */
    suspend fun removeById(id: String)

    /**
     * Querying Entities using specs
     */
    suspend fun query(
        specs: List<ISpecification> = listOf()
    ): EntitiesList<ENTITY>

    /**
     * Get items count using specs
     */
    suspend fun getItemsCount(
        specs: List<ISpecification> = listOf()
    ): Long

//    suspend fun slice(columnName: String, existedSlices: List<SliceValue<Any>> = listOf()): List<SliceValue<*>>
}