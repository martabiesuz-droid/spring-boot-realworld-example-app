# Records adoption decision (issue #5)

The issue asks to introduce record types "when possible". This codebase has a hybrid
persistence layer (JPA for writes, MyBatis for reads), which constrains where records are safe.

## Converted to record
- `ArticleFavoriteCount`  already immutable (Lombok @Value) and mapped by MyBatis via a
  `<constructor>` resultMap, so the record's canonical constructor maps cleanly.

## NOT converted (with reason)
- `ArticleData`, `ProfileData`, `CommentData`  populated by MyBatis `resultMap` using
  setter-based `<result property="...">` mappings. Records are immutable (no setters), so
  converting them would break the read mappings without rewriting the MyBatis XML.
- `UserData`  mapped by MyBatis `resultType` (compatible), but consumed across the codebase
  via Lombok-style getters (`getEmail()`, etc.); converting would require touching multiple
  consumers. Deferred to keep this change low-risk.
- `NewArticleParam`, `UpdateArticleParam`, `RegisterParam`, `UpdateUserParam`  request-binding
  objects requiring a no-args constructor (Jackson deserialization + Bean Validation). Records
  have no no-args constructor, so they remain classes.
- JPA `@Entity` classes (User, Article, Tag, Comment, FollowRelation, ArticleFavorite)  JPA
  requires a mutable class with a no-args constructor; entities cannot be records.
