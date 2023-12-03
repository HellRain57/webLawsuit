-- liquibase formatted sql

-- changeset developer:JIRAID-001-1 logicalFilePath:-
CREATE TABLE product (
    id       UUID         DEFAULT gen_random_uuid() PRIMARY KEY,
    title    VARCHAR(32)  NOT NULL,
    created  TIMESTAMPTZ  NOT NULL
);