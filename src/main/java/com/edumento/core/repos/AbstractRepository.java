package com.edumento.core.repos;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import com.edumento.core.domain.AbstractEntity;

/** Created by ahmad on 5/3/16. */
@NoRepositoryBean
public interface AbstractRepository<T extends AbstractEntity, ID extends Serializable>
		extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

	// @Override
	// @Query("update #{#entityName} e set e.deleted=true , e.deletedDate = UTC_TIMESTAMP, e.deletedBy = ?#{principal.username} where e.id=?1")
	// @Modifying
	// void deleteById(ID id);


	// @Override
	// @Query("update #{#entityName} e set e.deleted=true , e.deletedDate = UTC_TIMESTAMP, e.deletedBy = ?#{principal.username}  where e =?1")
	// @Modifying
	// void delete(T t);

	/**
	 * changed by A.Alsayed 5-2-2019 Purpose is to allow schedule tasks to delete entities.
	 */

	// @Override
	// @Query("update #{#entityName} e set e.deleted=true , e.deletedDate = UTC_TIMESTAMP,
	// e.deletedBy = ?#{principal.username} where e in ?1")
	// @Modifying
	// void delete(Iterable<? extends T> iterable);

	// @Override
	// default void deleteAll(Iterable<? extends T> iterable) {
	// 	iterable.forEach(entity -> {
	// 		entity.setDeleted(true);
	// 		entity.setDeletedDate(new Date());
	// 		entity.setDeletedBy(SecurityUtils.getCurrentUserLogin() == null ? "System"
	// 				: SecurityUtils.getCurrentUserLogin());
	// 	});
	// 	this.saveAll(iterable);
	// }

	// @Override
	// @Query("from #{#entityName} e  where e.id = ?1 and e.deleted=false")
	// Optional<T> findById(ID id);

	// @Override
	// @Query("from #{#entityName} e  where e.deleted=false")
	// List<T> findAll();

	// @Override
	// @Query("from #{#entityName} e  where e.id in ?1 and e.deleted=false ")
	// List<T> findAllById(Iterable<ID> iterable);

	// @Override
	// @Query("from #{#entityName} e  where e.deleted=false")
	// Page<T> findAll(Pageable pageable);
}
