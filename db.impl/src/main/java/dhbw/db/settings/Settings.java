package dhbw.db.settings;

public interface Settings {

	/**
	 * get path to artist header file
	 * 
	 * @return
	 */
	public String getArtistFile();

	/**
	 * get path to a specific artist data file
	 * 
	 * @param index
	 * @return
	 */
	public String getArtistFile(int index);

	/**
	 * get path to album header file
	 * 
	 * @return
	 */
	public String getAlbumFile();

	/**
	 * get path to a specific album data file
	 * 
	 * @param index
	 * @return
	 */
	public String getAlbumFile(int index);

	/**
	 * get path to connection header file
	 * 
	 * @return
	 */
	public String getConnectionFile();

	/**
	 * get path to a specific connection data file
	 * 
	 * @param index
	 * @return
	 */
	public String getConnectionFile(int index);

	/**
	 * get line limit per file
	 * 
	 * @return
	 */
	public int getLimitPerFile();
}
