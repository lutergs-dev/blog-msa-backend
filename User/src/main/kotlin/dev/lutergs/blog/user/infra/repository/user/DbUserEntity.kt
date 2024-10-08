package dev.lutergs.blog.user.infra.repository.user

import dev.lutergs.blog.user.domain.aggregate.User
import dev.lutergs.blog.user.domain.entity.NickName
import dev.lutergs.blog.user.infra.repository.account.DbAccountEntity
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "user")
class DbUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var accounts: List<DbAccountEntity> = mutableListOf()

    @Column(name = "nickname", nullable = false)
    var nickName: String = ""

    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now()

    fun toUser(): User = User(this.id, this.accounts.map { it.toAccount() }, NickName(this.nickName), createdAt)

    companion object {
        fun fromUser(user: User): DbUserEntity = DbUserEntity().apply {
            this.id = user.id
            this.accounts = user.accounts.map { DbAccountEntity.fromAccount(it) }
            this.nickName = user.nickName.value
            this.createdAt = user.createdAt
        }
    }
}