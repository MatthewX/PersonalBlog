package com.xcb.blog.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.xcb.blog.model.Article;
import com.xcb.blog.model.ArticleTag;
import com.xcb.blog.model.Tag;

@Mapper
public interface ArticleTagDao {
    String TABLE_NAME = " article_tag ";
    String INSERT_FIELDS = " article_id, tag_id ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    String TAG_FIELDS = " id, name, count ";
    String ARTICLE_FIELDS = " id, title, describes, content, created_Date, comment_Count , category ";

    @Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,") values (#{articleId},#{tagId})"})
    int insertArticleTag(ArticleTag articleTag);

    @Select({"select",TAG_FIELDS,"from tag where id in (select tag_id from article_tag where article_id=#{articleId})"})
    List<Tag> selectByArticleId(int articleId);

    @Select({"select",ARTICLE_FIELDS,"from article where id in (select article_id from article_tag where tag_id=#{tagId}) limit #{offset},#{limit}"})
    List<Article> selectByTagId(@Param("tagId") int tagId,@Param("offset") int offset, @Param("limit") int limit);

    @Select({"select count(id) from article where id in (select article_id from article_tag where tag_id=#{tagId})"})
    int selectArticleCountByTagId(@Param("tagId") int tagId);

    @Delete({"delete from",TABLE_NAME,"where id=#{id}"})
    void deleteById(int id);
}
