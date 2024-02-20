package com.edumento.discussion.services;

import static java.lang.Boolean.TRUE;

import java.util.Date;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.edumento.core.constants.CommentType;
import com.edumento.core.exception.NotFoundException;
import com.edumento.core.exception.NotPermittedException;
import com.edumento.core.model.ResponseModel;
import com.edumento.core.security.SecurityUtils;
import com.edumento.discussion.domain.Comment;
import com.edumento.discussion.domain.Like;
import com.edumento.discussion.model.discussion.CommentCreateModel;
import com.edumento.discussion.repos.CommentRepository;
import com.edumento.discussion.repos.mongodb.LikeRepository;
import com.edumento.space.repos.SpaceRepository;
import com.edumento.user.domain.User;
import com.edumento.user.repo.UserRepository;

/** Created by ahmad on 10/10/16. */
@Service
public class CommentService {
	private final CommentRepository commentRepository;
	private final LikeRepository likeRepository;
	private final UserRepository userRepository;

	@Autowired
	public CommentService(CommentRepository commentRepository, LikeRepository likeRepository,
			UserRepository userRepository, SpaceRepository spaceRepository) {
		this.commentRepository = commentRepository;
		this.likeRepository = likeRepository;
		this.userRepository = userRepository;
	}

	public Comment createComment(String id, User user, CommentCreateModel commentCreateModel, CommentType type,
			Long spaceId) {
		var comment = new Comment();
		comment.setBody(commentCreateModel.getCommentBody());
		comment.setUserId(user.getId());
		comment.setUserName(user.getUserName());
		comment.setUserFullName(user.getFullName());
		comment.setUserThumbnail(user.getThumbnail());
		comment.setParentId(id);
		comment.setType(type);
		comment.setSpaceId(spaceId);
		commentRepository.save(comment);
		return comment;
	}

	public ResponseModel updateComment(String id, String body) {
		return userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.map(new Function<User, ResponseModel>() {
					@Override
					public ResponseModel apply(User user) {
						return commentRepository.findOneByIdAndDeletedFalse(id).map(new Function<Comment, ResponseModel>() {
							@Override
							public ResponseModel apply(Comment comment) {
								if (comment.getUserId().equals(user.getId())) {
									comment.setBody(body);
									comment.setLastModifiedBy(SecurityUtils.getCurrentUserLogin());
									comment.setLastModifiedDate(new Date());
									commentRepository.save(comment);
									return ResponseModel.done();
								} else {
									throw new NotPermittedException();
								}
							}
						}).orElseThrow(new Supplier<NotFoundException>() {
							@Override
							public NotFoundException get() {
								return new NotFoundException("comment");
							}
						});
					}
				}).orElseThrow(NotFoundException::new);
	}

	public void deleteCommentbyParent(String id) {
		userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).ifPresent(new Consumer<User>() {
			@Override
			public void accept(User user) {
				var list = commentRepository.deleteByParentId(id);
				if (list != null && !list.isEmpty()) {
					likeRepository.deleteByParentIdIn(list.stream().map(Comment::getParentId).collect(Collectors.toSet()));
				}
			}
		});
	}

	public void deleteCommentbyParentIn(Iterable<String> ids) {
		userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin()).ifPresent(new Consumer<User>() {
			@Override
			public void accept(User user) {
				var list = commentRepository.deleteByParentIdIn(ids);
				if (list != null && !list.isEmpty()) {
					likeRepository.deleteByParentIdIn(list.stream().map(Comment::getParentId).collect(Collectors.toSet()));
				}
			}
		});
	}

	public void deleteComment(String id) {
		userRepository.findOneByUserNameAndDeletedFalse(SecurityUtils.getCurrentUserLogin())
				.ifPresent(new Consumer<User>() {
					@Override
					public void accept(User user) {
						commentRepository.findOneByIdAndDeletedFalse(id).ifPresent(new Consumer<Comment>() {
							@Override
							public void accept(Comment comment) {
								comment.setDeleted(TRUE);
								comment.setDeletedDate(new Date());
								commentRepository.delete(comment);
							}
						});
					}
				});
	}

	public ResponseModel toggleCommentLike(String id, User user) {
		return commentRepository.findOneByIdAndDeletedFalse(id).map(new Function<Comment, ResponseModel>() {
			@Override
			public ResponseModel apply(Comment comment) {
				return likeRepository
						.findOneByUserIdAndParentIdAndDeletedFalse(user.getId(), comment.getId()).map(new Function<Like, ResponseModel>() {
							@Override
							public ResponseModel apply(Like like) {
								likeRepository.delete(like);
								comment.getLikes().remove(like);
								comment.setVotes(comment.getVotes() - 1);
								commentRepository.save(comment);
								return ResponseModel.done(0);
							}
						}).orElseGet(new Supplier<ResponseModel>() {
							@Override
							public ResponseModel get() {
								var like = newLike(id, user.getId());
								like.setUserName(user.getUserName());
								like.setParentId(comment.getId());
								likeRepository.save(like);
								comment.getLikes().add(like);
								comment.setVotes(comment.getVotes() + 1);
								commentRepository.save(comment);
								return ResponseModel.done(1);
							}
						});
			}
		}).orElseThrow(new Supplier<NotFoundException>() {
					@Override
					public NotFoundException get() {
						return new NotFoundException("comment");
					}
				});
	}

	public Like newLike(String commentId, Long userId) {
		var like = new Like();
		like.setUserId(userId);
		like.setLiked(TRUE);
		like.setParentId(commentId);
		return like;
	}
}
