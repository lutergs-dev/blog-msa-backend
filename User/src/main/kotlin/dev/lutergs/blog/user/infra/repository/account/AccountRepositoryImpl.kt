package dev.lutergs.blog.user.infra.repository.account

import dev.lutergs.blog.user.domain.entity.Account
import dev.lutergs.blog.user.domain.repository.AccountRepository
import dev.lutergs.blog.user.domain.value.Email
import org.springframework.data.repository.findByIdOrNull

class AccountRepositoryImpl(
    private val repository: DbAccountEntityRepository
): AccountRepository {
    override fun findById(id: Long): Account? {
        return this.repository.findByIdOrNull(id)?.toAccount()
    }

    override fun findByEmail(email: Email): Account? {
        return this.repository.findByVendorAndLocalPart(email.vendor, email.localPart)?.toAccount()
    }
}