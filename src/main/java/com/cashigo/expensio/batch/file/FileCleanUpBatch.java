package com.cashigo.expensio.batch.file;

import com.cashigo.expensio.batch.file.model.FileMetaDataRecord;
import com.cashigo.expensio.common.consts.Status.FileStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class FileCleanUpBatch {

    @Value("${batch.chunk.size}")
    private int chunkSize;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;
    private final DataSource dataSource;

    @Bean("csvErrorFileCleanUp")
    public Job csvErrorFileCleanUp(
            @Qualifier("fileCleanUp") Step fileCleanUp
    ) {
        return new JobBuilder("CSV-ErrorFileCleanUp", jobRepository)
                .start(fileCleanUp)
                .build();
    }

    @Bean("fileCleanUp")
    public Step fileCleanUp(
            @Qualifier("readDueDateActiveFileMetaData") JdbcPagingItemReader<FileMetaDataRecord> itemReader
    ) {
        return new StepBuilder("CleanErrorFiles", jobRepository)
                .<FileMetaDataRecord, FileMetaDataRecord>chunk(chunkSize, platformTransactionManager)
                .reader(itemReader)
                .processor(cleanUpErrorFileAndUpdateStatus())
                .writer(updateStatusOfFilesMetaData())
                .faultTolerant()
                .skip(RuntimeException.class)
                .skipLimit(chunkSize/4)
                .build();
    }

    @Bean @StepScope
    public JdbcPagingItemReader<FileMetaDataRecord> readDueDateActiveFileMetaData(
            @Qualifier("dueExpiryActiveFileMetaDataQuery") SqlPagingQueryProviderFactoryBean pagingQuery,
            @Value("#{jobParameters['now']}") String now
    ) throws Exception {
        JdbcPagingItemReader<FileMetaDataRecord> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setFetchSize(chunkSize);
        reader.setQueryProvider(pagingQuery.getObject());
        reader.setRowMapper(fileMetaDataRecordRowMapper());
        reader.setParameterValues(Map.of("now", Instant.parse(now)));
        return reader;
    }

    @Bean @StepScope
    public ItemProcessor<FileMetaDataRecord, FileMetaDataRecord> cleanUpErrorFileAndUpdateStatus() {
        return record -> {
            String fileName = record.fileName();
            Path path = Path.of(record.path());
            Files.deleteIfExists(path);
            return new FileMetaDataRecord(fileName, path.toString(), FileStatus.DELETED);
        };
    }

    @Bean @StepScope
    JdbcBatchItemWriter<FileMetaDataRecord> updateStatusOfFilesMetaData() {
        return new JdbcBatchItemWriterBuilder<FileMetaDataRecord>()
                .dataSource(dataSource)
                .sql("UPDATE file_meta_data fmd set fmd.status = :status where fmd.stored_file_name = :fileName")
                .itemSqlParameterSourceProvider(record -> {
                    String fileName = record.fileName();
                    String status = record.fileStatus().name();
                    return new MapSqlParameterSource().addValues(Map.of(
                            "fileName", fileName,
                            "status", status
                    ));
                })
                .build();
    }

    @Bean(name = "dueExpiryActiveFileMetaDataQuery")
    SqlPagingQueryProviderFactoryBean dueExpiryActiveFileMetaDataQuery() {
        SqlPagingQueryProviderFactoryBean pagingQuery = new SqlPagingQueryProviderFactoryBean();
        pagingQuery.setDataSource(dataSource);
        pagingQuery.setSelectClause("""
            fmd.stored_file_name as fileName,
            fmd.file_path as path
        """);
        pagingQuery.setFromClause("from file_meta_data fmd");
        pagingQuery.setWhereClause("fmd.status = 'ACTIVE' and fmd.expires_at <= :now");
        pagingQuery.setSortKey("fileName");
        return pagingQuery;
    }

    RowMapper<FileMetaDataRecord> fileMetaDataRecordRowMapper() {
        return (rs, index) -> {
            String fileName = rs.getString("fileName");
            String path = rs.getString("path");
            return new FileMetaDataRecord(fileName, path, FileStatus.ACTIVE);
        };
    }
}
