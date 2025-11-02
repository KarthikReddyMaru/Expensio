package com.cashigo.expensio.repository;

import com.cashigo.expensio.model.ImportMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ImportMetaDataRepository extends JpaRepository<ImportMetaData, UUID> {
}
