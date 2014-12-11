package models

import models.core.TypesafeID.driver.simple._
import models.eveapi.account.{ApiKeyInfoTable, AccountStatusTable}
import models.eveapi.character.{CharacterLocationTable, CharacterAssetItemTable, CharacterAssetListTable}
import models.eveapi.corporation.{CorporationLocationTable, CorporationAssetListTable, CorporationAssetItemTable}
import models.eveapi.eve.{CharactersToApiKeysTable, CorporationTable, CharacterTable}
import models.eveats.{UsersToApiKeysTable, UserTable, ApiKeyTable}

/**
 * DDL helper object.
 * List of all tableQuery objects to help play-Slick to create migrations
 *
 */
object DDL {
  val apiKeyTable = TableQuery[ApiKeyTable]
  val userTable = TableQuery[UserTable]
  val usersToApiKeyTable = TableQuery[UsersToApiKeysTable]

  val accountStausTable = TableQuery[AccountStatusTable]
  val apiKeyInfoTable = TableQuery[ApiKeyInfoTable]

  val characterTable = TableQuery[CharacterTable]
  val corporationTable = TableQuery[CorporationTable]
  val characterToApiKeyTable = TableQuery[CharactersToApiKeysTable]

  val corporationAssetListTable = TableQuery[CorporationAssetListTable]
  val corporationAssetItemTable = TableQuery[CorporationAssetItemTable]
  val corporationLocationTable = TableQuery[CorporationLocationTable]

  val characterAssetListTable = TableQuery[CharacterAssetListTable]
  val characterAssetItemTable = TableQuery[CharacterAssetItemTable]
  val characterLocationTable = TableQuery[CharacterLocationTable]
}
