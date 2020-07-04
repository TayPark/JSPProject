# JSP를 이용한 답글형 게시판 구현

개발환경: Eclipse EE
Java version: jdk 14
DB: Oracle 18c XE

## 프로젝트 간단 개요

- `MVC2`로 개발
- `jdbc`와 `Oracle dbcp`를 이용한 Oracle 18c XE 커넥션 관리
- Dispatcher를 이용한 Routing, Request에 대한 적절한 Response 구현
- Session 관리

## 내용

- 회원가입, 로그인
- 로그인 유무에 따른 게시판 CRUD 및 답글 기능 구현