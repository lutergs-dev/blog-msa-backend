package dev.lutergs.blog.user.infra.repository

import dev.lutergs.blog.user.domain.entity.Account
import dev.lutergs.blog.user.domain.repository.AccountRepository
import dev.lutergs.blog.user.domain.value.Vendor

class AccountRepositoryImpl: AccountRepository {
    override fun findById(id: Long): Account? {
        TODO("Not yet implemented")
    }

    override fun getAccountByVendorAndLocalPart(vendor: Vendor, localPart: String): Account? {
        TODO("Not yet implemented")
    }

    override fun saveAccount(account: Account): Account {
        TODO("Not yet implemented")
    }

    override fun createAccountByVendorAndLocalPart(vendor: Vendor, localPart: String): Account {
        TODO("Not yet implemented")
    }
}