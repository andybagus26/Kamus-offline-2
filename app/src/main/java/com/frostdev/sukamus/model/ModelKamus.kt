package com.frostdev.sukamus.model

import java.io.Serializable

class ModelKamus(
    val id: Int = 0,
    val kata: String?,
    val deskripsi: String?
) : Serializable