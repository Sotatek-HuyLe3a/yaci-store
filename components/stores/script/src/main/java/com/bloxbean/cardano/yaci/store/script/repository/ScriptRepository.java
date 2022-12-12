package com.bloxbean.cardano.yaci.store.script.repository;

import com.bloxbean.cardano.yaci.store.script.model.Script;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScriptRepository extends JpaRepository<Script, String> {
}