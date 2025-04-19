package spirit.realm.faefinance.data.classes

data class TransactionGroupDate(
    val year: Int,
    val month: Int
)

data class TransactionGroup(
    val groupDate: TransactionGroupDate,
    val accounts: List<TransactionExpanded>
)
