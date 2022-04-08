CREATE TABLE "GistFiles" (
  "fileId" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "gistId" TEXT,
  "filename" TEXT,
  "content" TEXT,
  "gitHubVersion" TEXT,
  "dirty" INT DEFAULT 0,
  CONSTRAINT "fk_GistFiles_Gists_1" FOREIGN KEY ("gistId") REFERENCES "Gists" ("gistId") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "Unique" UNIQUE ("gistId", "fileName") ON CONFLICT IGNORE
);

CREATE TABLE "Gists" (
  "gistId" TEXT NOT NULL ON CONFLICT ABORT,
  "description" TEXT,
  "isPublic" INT,
  "url" TEXT,
  PRIMARY KEY ("gistId"),
  CONSTRAINT "gistId" UNIQUE ("gistId") ON CONFLICT IGNORE
);

CREATE TABLE "Metadata" (
    "id" INT,
    "jsonString" MEDIUMTEXT
);
