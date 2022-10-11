package domain

class NotFoundInRepositoryException(what: String = "", repository: String = "") : RuntimeException() {
    override val message: String = "$what not found in repository $repository"
}