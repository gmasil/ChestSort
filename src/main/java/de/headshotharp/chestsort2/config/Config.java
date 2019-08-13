package de.headshotharp.chestsort2.config;

public class Config {
	private DatabaseConfig database = new DatabaseConfig();

	public DatabaseConfig getDatabase() {
		return database;
	}

	public void setDatabase(DatabaseConfig database) {
		this.database = database;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((database == null) ? 0 : database.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Config other = (Config) obj;
		if (database == null) {
			if (other.database != null) {
				return false;
			}
		} else if (!database.equals(other.database)) {
			return false;
		}
		return true;
	}

	public static class DatabaseConfig {
		private String driver;
		private String dialect;
		private String url;
		private String username;
		private String password;

		public String getDriver() {
			return driver;
		}

		public void setDriver(String driver) {
			this.driver = driver;
		}

		public String getDialect() {
			return dialect;
		}

		public void setDialect(String dialect) {
			this.dialect = dialect;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((dialect == null) ? 0 : dialect.hashCode());
			result = prime * result + ((driver == null) ? 0 : driver.hashCode());
			result = prime * result + ((password == null) ? 0 : password.hashCode());
			result = prime * result + ((url == null) ? 0 : url.hashCode());
			result = prime * result + ((username == null) ? 0 : username.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			DatabaseConfig other = (DatabaseConfig) obj;
			if (dialect == null) {
				if (other.dialect != null) {
					return false;
				}
			} else if (!dialect.equals(other.dialect)) {
				return false;
			}
			if (driver == null) {
				if (other.driver != null) {
					return false;
				}
			} else if (!driver.equals(other.driver)) {
				return false;
			}
			if (password == null) {
				if (other.password != null) {
					return false;
				}
			} else if (!password.equals(other.password)) {
				return false;
			}
			if (url == null) {
				if (other.url != null) {
					return false;
				}
			} else if (!url.equals(other.url)) {
				return false;
			}
			if (username == null) {
				if (other.username != null) {
					return false;
				}
			} else if (!username.equals(other.username)) {
				return false;
			}
			return true;
		}
	}
}
