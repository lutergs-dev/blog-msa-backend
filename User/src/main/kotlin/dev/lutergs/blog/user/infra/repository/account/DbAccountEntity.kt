package dev.lutergs.blog.user.infra.repository.account

import dev.lutergs.blog.user.domain.entity.Account
import dev.lutergs.blog.user.domain.value.Email
import dev.lutergs.blog.user.domain.value.Vendor
import dev.lutergs.blog.user.infra.repository.user.DbUserEntity
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "account")
class DbAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: DbUserEntity = DbUserEntity()

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var vendor: Vendor = Vendor.GOOGLE

    @Column(name = "local_part", nullable = false)
    var localPart: String = ""

    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now()

    fun toAccount(): Account = Account(this.id!!, Email(this.localPart, this.vendor), this.createdAt)

    companion object {
        fun fromAccount(account: Account): DbAccountEntity = DbAccountEntity().apply {
            this.id = account.id
            this.user
        }
    }
}
