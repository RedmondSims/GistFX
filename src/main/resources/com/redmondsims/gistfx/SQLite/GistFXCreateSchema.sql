CREATE TABLE "GistFiles" (
  "fileId" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "gistId" TEXT,
  "filename" TEXT,
  "content" TEXT,
  "dirty" INT DEFAULT 0,
  "uploadDate" DATE,
  CONSTRAINT "fk_GistFiles_Gists_1" FOREIGN KEY ("gistId") REFERENCES "Gists" ("gistId") ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT "Unique" UNIQUE ("gistId", "fileName") ON CONFLICT IGNORE
);

CREATE TABLE "Gists" (
  "gistId" TEXT NOT NULL ON CONFLICT ABORT,
  "name" TEXT,
  "description" TEXT,
  "isPublic" INT,
  "url" TEXT,
  PRIMARY KEY ("gistId"),
  CONSTRAINT "gistId" UNIQUE ("gistId") ON CONFLICT IGNORE
);

CREATE TABLE "GistUndoFiles" (
  "IDName" TEXT NOT NULL,
  "gistId" text,
  "content" TEXT,
  PRIMARY KEY ("IDName"),
  CONSTRAINT "fk_GistUndoFiles_Gists_1" FOREIGN KEY ("gistId") REFERENCES "Gists" ("gistId") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE "NameMap" (
  "gistId" TEXT,
  "name" TEXT,
  CONSTRAINT "fk_NameMap_Gists_1" FOREIGN KEY ("gistId") REFERENCES "Gists" ("gistId") ON DELETE CASCADE ON UPDATE CASCADE
);

