package persistence

sealed class Column(val columnName: String) {
    object SampleType : Column("sample_type")

}