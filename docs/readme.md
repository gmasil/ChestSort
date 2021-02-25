# ChestSort Documentation

- [Features](#features)
- [Usage](#usage)
  - [Creating Signs](#creating-signs)
  - [Deleting Signs](#deleting-signs)
  - [Marking Chest](#marking-chest)
  - [Create/Register Chests](#create-register-chests)
  - [Unregister/Delete Chests](#unregister-delete-chests)
  - [Insert Materials](#insert-materials)
  - [Reset Warehouse](#reset-warehouse)
- [Configuration](#configuration)
  - [Database](#database)
    - [MySQL 5.7](#mysql-57)
    - [MySQL 8](#mysql-8)
    - [PostgreSQL](#postgresql)
    - [Save to File](#save-to-file)
  - [Permissions](#permissions)

## Features

With ChestSort you can assign a single or multiple materials to chests in Minecraft and the automatically insert items from your inventory. You can create special ChestSort signs with wich you can insert the items(s) in you hand into your warehouse by rightlicking it. Additionally you can use a command to insert all matching items from your inventory into your warehouse, but you have to be near a ChestSort sign.

There is a central warehouse for all players and each player can create their own individual warehouse.

## Usage

### Creating Signs

You can create ChestSort signs by placing a sign and inserting `[ChestSort]` into the first line (mind the square brackets and upper/lowercase).
Use an **oakwood sign** for the central warehouse and **birchwood signs** for your individual warehouse.
When you create the sign it will be colored and will be either marked as central sign or will get your player name to identify it later on.

### Deleting Signs

If you want to delete a sign you can mark it just like a chest. While holding a **stick** in your main hand right click the sign. Now you can delete it with one of the following commands, depending on the type of the chest.

Deleting your personal (user) sign:

```bash
/chestsort delete user
```

Deleting a central sign:

```bash
/chestsort delete central
```

### Marking Chest

You have to hold a **stick** in your main hand and right click chests with it. If the chest just opens you dont have permissions to manage warehouses. Otherwise you will see a green text confirming that you marked a chest.

### Create/Register Chests

After marking a chest you can register it to your warehouse. If you want to register it to your personal (user) warehouse you can use the following command:

```bash
/chestsort create user STONE # here you can enter every existing material
```

If you want to register that chest to the central warehouse for all users use the following command:

```bash
/chestsort create central COBBLESTONE
```

### Unregister/Delete Chests

After marking the chest you want to delete use the following command to delete your personal (user) chest:

```bash
/chestsort delete user
```

Deleting a central chest:

```bash
/chestsort delete central
```

This will only do a dry-run of the deletion and tell you what would be deleted. If you you want to confirm the command just repeat the same command with an `confirm` at the end like:

```bash
/chestsort delete central confirm
```

### Insert Materials

To insert materials into a warehouse you have to be close to a ChestSort sign. If you want to insert into the central warehouse you must use a central sign, for your personal warehouse you need a personal sign.

You can insert the item/items in your hand by rightclicking on a ChestSort sign. All the materials will be ordered perfectly into the belonging warehouse depending on the sign (central/user).

If you want to insert all materials from your inventory at once you can use a command. If you want to insert everything from your inventory into the central warehouse you have to be close (about 15 blocks) to a central ChestSort sign and use the command

```bash
/chestsort all central
```

The same applies to your personal warehouse. Stand close to one of your personal ChestSort signs and type

```bash
/chestsort all user
```

### Reset Warehouse

If you want to start all over with your warehouse you don't have to delete all chests and signs manually. You can use the `/chestsort reset` command to do so. As all of the other commands you have to choose either the central warehouse or your personal one. The next parameter will decide if only chests/signs or both will be deleted.

```bash
/chestsort reset <central/user> <all/signs/chests>
```

Like the delete command you will see a how many chests and signs will be deleted. If you are happy with the results you can confirm the command, for example like this:

```bash
/chestsort reset central chests confirm
```

## Configuration

### Database

Place the [ChestSort.jar](https://jenkins.gmasil.de/job/gmasil/job/ChestSort/job/master/lastSuccessfulBuild/artifact/target/ChestSort.jar) inside your plugins folder of your server (bukkit, spigot etc.) and start your server. The plugin will create a `ChestSort` folder inside your plugins folder and create a default config file called `config.yml`. In that file you can specify your database connection. By default ChestSort supports MySQL, PostgreSQL and H2, but this list can easily be expanded if required. I cannot provide an Oracle driver due to license regulations, but you can add them manually into the jar file.

The following `config.yml` files will show example configurations for some SQL servers. Under the hood the entity manager **Hibernate** is used, so if you want to find out which values are allowed, use google with search terms like _"hibernate postgresql configuration"_.

#### MySQL 5.7

```yml
database:
  driver: "com.mysql.cj.jdbc.Driver"
  dialect: "org.hibernate.dialect.MySQL57Dialect"
  url: "jdbc:mysql://localhost:3306/databasename?useSSL=false&serverTimezone=UTC"
  username: "my-username"
  password: "my-secret-password"
```

#### MySQL 8

```yml
database:
  driver: "com.mysql.cj.jdbc.Driver"
  dialect: "org.hibernate.dialect.MySQL8Dialect"
  url: "jdbc:mysql://localhost:3306/databasename?useSSL=false&serverTimezone=UTC"
  username: "my-username"
  password: "my-secret-password"
```

#### PostgreSQL

```yml
database:
  driver: "org.postgresql.Driver"
  dialect: "org.hibernate.dialect.PostgreSQLDialect"
  url: "jdbc:postgresql://localhost:5432/databasename"
  username: "my-username"
  password: "my-secret-password"
```

#### Save to File

If you don't have a database and you are not willing to install one, you can use the H2 database that comes with ChestSort. This database is designed for software tests, but can also be used to save an in-memory database to your filesystem. **This is not recommended!** Normally this should work fine, but you might loose all your data when the plugin or your server crashes, so be warned.

The following example `config.yml` will tell ChestSort to start the H2 database for you and store all the data in the folder `chestsort-data`:

```yml
database:
  driver: "org.h2.Driver"
  dialect: "org.hibernate.dialect.H2Dialect"
  url: "jdbc:h2:file:./chestsort-data"
  username: "my-username"
  password: "my-secret-password"
```

### Permissions

Currently there are three permissions to give to your players:

- `chestsort.manage`
- `chestsort.manage.central`
- `chestsort.reset`

Grant the permission `chestsort.manage` to all the players which should be allowed to create a personal (user) warehouse. This can savely be given to all players.

The permission `chestsort.manage.central` allows users to create and delete chests and signs for the central warehouse. Give this permissions to your trusted players and admins. All users can insert into the central warehouse, there is no separate permission for it yet.

The `chestsort.reset` permission will allow players to delete all central chests and or signs with a single command in case you want to start over or extensively rework your central warehouse. This permission should only be granted to your admins, if at all. This permission affects the central warehouse only. All players are allowed to reset their own (user) warehouse.
