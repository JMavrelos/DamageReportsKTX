package gr.blackswamp.damagereports.logic.model

import gr.blackswamp.damagereports.ui.model.Report
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ReportData(
    override val id: UUID,
    override val name: String = "",
    override val description: String = "",
    val brand: BrandData?,
    val model: ModelData?,
    override val created: Date,
    override val changed: Boolean = false
) : Report {
    @IgnoredOnParcel
    override val brandName: String? = brand?.name

    @IgnoredOnParcel
    override val modelName: String? = model?.name
}