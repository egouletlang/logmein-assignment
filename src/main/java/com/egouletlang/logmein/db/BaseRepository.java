package com.egouletlang.logmein.db;

import com.egouletlang.logmein.model.DbModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BaseRepository<T extends DbModel> extends MongoRepository<T, String> {

}