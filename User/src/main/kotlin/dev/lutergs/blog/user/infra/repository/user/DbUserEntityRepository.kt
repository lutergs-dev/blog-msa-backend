package dev.lutergs.blog.user.infra.repository.user

import org.springframework.data.repository.CrudRepository

interface DbUserEntityRepository: CrudRepository<DbUserEntity, Long>