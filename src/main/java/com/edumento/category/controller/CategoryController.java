package com.edumento.category.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.edumento.category.model.CreateCategoryModel;
import com.edumento.category.services.CategoryService;
import com.edumento.core.constants.SortDirection;
import com.edumento.core.constants.SortField;
import com.edumento.core.controller.abstractcontroller.AbstractController;
import com.edumento.core.model.PageRequestModel;
import com.edumento.core.model.ResponseModel;

/** Created by ahmad on 3/13/16. */
@RestController
@RequestMapping("/api/category")
public class CategoryController extends AbstractController<CreateCategoryModel, Long> {
	@Autowired
	CategoryService categoryService;

	@Override
	@RequestMapping(method = RequestMethod.POST)
	// @ApiOperation(value = "Create Category", notes = "this method is used to
	// create new category")
	public ResponseModel create(@RequestBody @Validated CreateCategoryModel createCategoryModel) {
		return categoryService.createCategory(createCategoryModel);
	}

	@RequestMapping(method = RequestMethod.GET)
	// @ApiOperation(value = "List Categories", notes = "this method is used to list
	// categories")
	public ResponseModel getAll(@RequestHeader(required = false, defaultValue = "0") Integer page,
			@RequestHeader(required = false, defaultValue = "100") Integer size,
			@RequestParam(required = false) String filter,
			@RequestHeader(required = false, defaultValue = "NAME") SortField field,
			@RequestHeader(required = false, defaultValue = "ASCENDING") SortDirection sortDirection,
			@RequestHeader(required = false, defaultValue = "false") boolean all,
			@RequestHeader(name = "lang", required = false, defaultValue = "en") String lang) {
		return categoryService.getCategories(
				PageRequestModel.getPageRequestModel(page, size,
						Sort.by(sortDirection.getValue(), field.getFieldName())),
				filter, all, lang);
	}

	@RequestMapping(method = RequestMethod.GET, path = "/related_user_spaces")
	// @ApiOperation(value = "List Categories related with user Spaces", notes =
	// "this method is used
	// to list categories related with user Spaces")
	public ResponseModel getRelatedCategoryWithSpaceForUserAndDeletedFalse() {
		return categoryService.getUserCategoriesRelatedWithSpaces();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/cloud")
	// @ApiOperation(
	// value = "List Cloud Category",
	// notes = "this method is used to list cloud's categories"
	// )
	public ResponseModel getCloudCategories() {
		return categoryService.getCloudCategories();
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.PUT)
	// @ApiOperation(value = "Update Category", notes = "this method is used to
	// update category data")
	public ResponseModel update(@RequestBody @Validated CreateCategoryModel createCategoryModel,
			@PathVariable Long id) {
		return categoryService.updateCategory(createCategoryModel, id);
	}

	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	// @ApiOperation(value = "Get Category", notes = "this method is used to get
	// specific category")
	public ResponseModel getCategory(@PathVariable Long id) {
		return categoryService.getCategory(id);
	}

	@Override
	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
	// @ApiOperation(
	// value = "Delete Category",
	// notes = "this method is used to delete specific category"
	// )
	public ResponseModel delete(@PathVariable Long id) {
		return categoryService.delete(id);
	}

}
