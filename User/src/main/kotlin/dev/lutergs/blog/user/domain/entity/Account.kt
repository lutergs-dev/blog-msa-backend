package dev.lutergs.blog.user.domain.entity

import dev.lutergs.blog.user.domain.value.Vendor

class Account (
    val vendor: Vendor,
    val localPart: String
){
}