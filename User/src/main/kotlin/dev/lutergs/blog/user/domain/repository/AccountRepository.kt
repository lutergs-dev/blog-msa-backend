package dev.lutergs.blog.user.domain.repository

import dev.lutergs.blog.user.domain.entity.Account
import dev.lutergs.blog.user.domain.value.Vendor

interface AccountRepository {
    fun findById(id: Long): Account?
    fun getAccountByVendorAndLocalPart(vendor: Vendor, localPart: String): Account?
    fun saveAccount(account: Account): Account
    fun createAccountByVendorAndLocalPart(vendor: Vendor, localPart: String): Account
}