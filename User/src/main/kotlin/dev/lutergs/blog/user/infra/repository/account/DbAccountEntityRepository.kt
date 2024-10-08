package dev.lutergs.blog.user.infra.repository.account

import dev.lutergs.blog.user.domain.value.Vendor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface DbAccountEntityRepository: CrudRepository<DbAccountEntity, Long> {
    fun findByVendorAndLocalPart(vendor: Vendor, localPart: String): DbAccountEntity?

    @Query("SELECT a FROM DbAccountEntity a JOIN FETCH a.user u JOIN FETCH u.accounts WHERE a.id = :accountId")
    fun findAccountWithUserAndAccounts(@Param("accountId") accountId: Long): DbAccountEntity?
}