package ao.app.boleia.motorista.model;

/**
 * Created by maike.silva on 16/03/2018.
 */

public class LogAutomovel {

    private String accuracy,altitude,cdautomovel,cdviagem,locationprovider,provider,sqlog,time;
    private Double latitude,longitude;
    private Long dthrlog;
    private float bearing,speed;

    public Long getDthrlog() {
        return dthrlog;
    }

    public void setDthrlog(Long dthrlog) {
        this.dthrlog = dthrlog;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getAltitude() {
        return altitude;
    }

    public void setAltitude(String altitude) {
        this.altitude = altitude;
    }

    public String getCdautomovel() {
        return cdautomovel;
    }

    public void setCdautomovel(String cdautomovel) {
        this.cdautomovel = cdautomovel;
    }

    public String getCdviagem() {
        return cdviagem;
    }

    public void setCdviagem(String cdviagem) {
        this.cdviagem = cdviagem;
    }

    public String getLocationprovider() {
        return locationprovider;
    }

    public void setLocationprovider(String locationprovider) {
        this.locationprovider = locationprovider;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getSqlog() {
        return sqlog;
    }

    public void setSqlog(String sqlog) {
        this.sqlog = sqlog;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
