package domain

interface IBaseDao<ENTITY: IEntity> {
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
     * Remove Entity by [id]
     */
    suspend fun removeById(id: String)

    /**
     * Querying Entities using specs
     */
    suspend fun query(
        filterSpec: ISpecification? = null,
        sortingSpec: ISpecification? = null,
        pagingSpec: ISpecification? = null,
        searchSpec: ISpecification? = null,
        groupingSpec: ISpecification? = null
    ): EntitiesList<ENTITY>

    /**
     * Get items count using specs
     */
    suspend fun getItemsCount(
        filterSpec: ISpecification? = null,
        sortingSpec: ISpecification? = null,
        pagingSpec: ISpecification? = null,
        searchSpec: ISpecification? = null,
        groupingSpec: ISpecification? = null
    ): Long

//    suspend fun slice(columnName: String, existedSlices: List<SliceValue<Any>> = listOf()): List<SliceValue<*>>
}