package TNTmusic.models;

import java.io.Serializable;

public class Cancion extends Object implements Serializable{

  private int id;
  private String artist_name;
  private String track_name;
  private String track_id;
  private short popularity;
  private short year;
  private String genre;
  private double danceability;
  private double energy;
  private byte key;
  private double loudness;
  private byte mode;
  private double speechiness;
  private double acousticness;
  private double instrumentalness;
  private double liveness;
  private double valence;
  private double tempo;
  private int duration_ms;
  private byte time_signature;

  public Cancion(String track_name) {
    this.track_name = track_name;
  }

  public Cancion(String track_name, String artist_name) {
    this.track_name = track_name;
    this.artist_name = artist_name;
  }

  public Cancion(int id, String artist_name, String track_name, String track_id, 
      short popularity, short year, String genre, double danceability, 
      double energy, byte key, double loudness, byte mode,
      double speechiness, double acousticness, double instrumentalness, double liveness,
      double valence, double tempo, int duration_ms, byte time_signature) {
    this.id = id;
    this.artist_name = artist_name;
    this.track_name = track_name;
    this.track_id = track_id;
    this.popularity = popularity;
    this.year = year;
    this.genre = genre;
    this.danceability = danceability;
    this.energy = energy;
    this.key = key;
    this.loudness = loudness;
    this.mode = mode;
    this.speechiness = speechiness;
    this.acousticness = acousticness;
    this.instrumentalness = instrumentalness;
    this.liveness = liveness;
    this.valence = valence;
    this.tempo = tempo;
    this.duration_ms = duration_ms;
    this.time_signature = time_signature;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getArtist_name() {
    return artist_name;
  }

  public void setArtist_name(String artist_name) {
    this.artist_name = artist_name;
  }

  public String getTrack_name() {
    return track_name;
  }

  public void setTrack_name(String track_name) {
    this.track_name = track_name;
  }

  public String getTrack_id() {
    return track_id;
  }

  public void setTrack_id(String track_id) {
    this.track_id = track_id;
  }

  public short getPopularity() {
    return popularity;
  }

  public void setPopularity(short popularity) {
    this.popularity = popularity;
  }

  public short getYear() {
    return year;
  }

  public void setYear(short year) {
    this.year = year;
  }

  public String getGenre() {
    return genre;
  }

  public void setGenre(String genre) {
    this.genre = genre;
  }

  public double getDanceability() {
    return danceability;
  }

  public void setDanceability(double danceability) {
    this.danceability = danceability;
  }

  public double getEnergy() {
    return energy;
  }

  public void setEnergy(double energy) {
    this.energy = energy;
  }

  public byte getKey() {
    return key;
  }

  public void setKey(byte key) {
    this.key = key;
  }

  public double getLoudness() {
    return loudness;
  }

  public void setLoudness(double loudness) {
    this.loudness = loudness;
  }

  public byte getMode() {
    return mode;
  }

  public void setMode(byte mode) {
    this.mode = mode;
  }

  public double getSpeechiness() {
    return speechiness;
  }

  public void setSpeechiness(double speechiness) {
    this.speechiness = speechiness;
  }

  public double getAcousticness() {
    return acousticness;
  }

  public void setAcousticness(double acousticness) {
    this.acousticness = acousticness;
  }

  public double getInstrumentalness() {
    return instrumentalness;
  }

  public void setInstrumentalness(double instrumentalness) {
    this.instrumentalness = instrumentalness;
  }

  public double getLiveness() {
    return liveness;
  }

  public void setLiveness(double liveness) {
    this.liveness = liveness;
  }

  public double getValence() {
    return valence;
  }

  public void setValence(double valence) {
    this.valence = valence;
  }

  public double getTempo() {
    return tempo;
  }

  public void setTempo(double tempo) {
    this.tempo = tempo;
  }

  public int getDuration_ms() {
    return duration_ms;
  }

  public void setDuration_ms(int duration_ms) {
    this.duration_ms = duration_ms;
  }

  public byte getTime_signature() {
    return time_signature;
  }

  public void setTime_signature(byte time_signature) {
    this.time_signature = time_signature;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Cancion)  {
      Cancion c = (Cancion) obj;
      return this.getId() == c.getId();
    }
    return false;
  }
  
  @Override
  public String toString() {
    return this.id + ", " + this.track_name + ", " + this.artist_name + ", " + this.popularity + ", " + this.year; 
  }

}

