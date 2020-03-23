package io.zbx.repositories;

import io.zbx.models.Token;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends CrudRepository<Token, Long> {

    @Query("FROM Token t where t.id = :subjectID")
    Optional<Token> findBySubjectID(@Param("subjectID") String subjectID);
}
