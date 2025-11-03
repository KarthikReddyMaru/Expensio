package com.cashigo.expensio.batch.file.model;

import com.cashigo.expensio.common.consts.Status.FileStatus;

public record FileMetaDataRecord(String fileName, String path, FileStatus fileStatus) {
}
