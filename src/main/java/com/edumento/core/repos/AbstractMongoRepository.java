package com.edumento.core.repos;

import java.io.Serializable;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.edumento.core.domain.AbstractEntity;

@NoRepositoryBean
public interface AbstractMongoRepository<T extends AbstractEntity, ID extends Serializable>
		extends MongoRepository<T, ID> {
	// @Override
	// default void delete(T arg0) {
	// arg0.setDeleted(true);
	// arg0.setDeletedDate(new Date());
	// arg0.setDeletedBy(SecurityUtils.getCurrentUserLogin());
	// this.save(arg0);
	// }

	// @Override
	// default void deleteById(ID arg0) {
	// T entity = this.findById(arg0).orElseThrow(NotFoundException::new);
	// entity.setDeleted(true);
	// entity.setDeletedDate(new Date());
	// entity.setDeletedBy(SecurityUtils.getCurrentUserLogin());
	// this.save(entity);
	// }

	// @Override
	// default void deleteAll(Iterable<? extends T> arg0) {
	// arg0.forEach(entity -> {
	// entity.setDeleted(true);
	// entity.setDeletedDate(new Date());
	// entity.setDeletedBy(SecurityUtils.getCurrentUserLogin());
	// });
	// this.saveAll(arg0);
	// }
}
