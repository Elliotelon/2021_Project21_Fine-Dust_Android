package fastcampus.aop.part4.chapter06.data.models.tmcoordinates


import com.google.gson.annotations.SerializedName

data class Meta(
    @SerializedName("total_count")
    val totalCount: Int?
)