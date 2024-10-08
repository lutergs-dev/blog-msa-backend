package dev.lutergs.blog.user.infra.repository.user

import dev.lutergs.blog.user.domain.aggregate.User
import dev.lutergs.blog.user.domain.entity.Account
import dev.lutergs.blog.user.domain.entity.NickName
import dev.lutergs.blog.user.domain.repository.UserRepository
import dev.lutergs.blog.user.domain.value.Email
import dev.lutergs.blog.user.infra.repository.account.DbAccountEntity
import dev.lutergs.blog.user.infra.repository.account.DbAccountEntityRepository
import org.springframework.data.repository.findByIdOrNull
import java.time.OffsetDateTime

class UserRepositoryImpl(
    private val repository: DbUserEntityRepository,
    private val accountRepository: DbAccountEntityRepository
): UserRepository {
    override fun findUserByAccount(account: Account): User? {
        return this.accountRepository.findAccountWithUserAndAccounts(account.id)?.user?.toUser()
    }

    override fun saveUser(user: User): User {
        val dbUser = DbUserEntity.fromUser(user)
        return this.repository.save(dbUser).toUser()
    }

    override fun createUserByEmail(email: Email): User {
        val dbUser = DbUserEntity().apply {
            this.id = null
            this.accounts = mutableListOf()
            this.nickName = NickName.createRandomNickName().value
            this.createdAt = OffsetDateTime.now()
        }.let { this.repository.save(it) }

        val dbAccount = DbAccountEntity().apply {
            this.id = null
            this.user = dbUser
            this.vendor = email.vendor
            this.localPart = email.localPart
            this.createdAt = OffsetDateTime.now()
        }.let { this.accountRepository.save(it) }

        return this.repository.findByIdOrNull(dbUser.id!!)!!.toUser()
    }
}