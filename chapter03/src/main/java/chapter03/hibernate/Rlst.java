package chapter03.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name="white_apps_se_ruleset")
public class Rlst{

    @Id
    private int index;

    @Column(name = "APP ID" )
    private String app_id;
    @Column(name = "Application Name" )
    private String application_name;
    @Column(name = "Change Type" )
    private String change_type;
    @Column(name = "Comment" )
    private String comment;
    @Column(name = "FQDNs" )
    private String fqdns;
    @Column(name = "IPs" )
    private String ips;
    @Column(name = "Protocol type port" )
    private String protocol_type_port;
    @Column(name = "TSA" )
    private String tsa_expiration_date;

    public Rlst(){

    }

    public Rlst(String app_id, String application_name, String change_type, String comment, String fqdns, String ips, String protocol_type_port, String tsa_expiration_date) {
        this.app_id = app_id;
        this.application_name = application_name;
        this.change_type = change_type;
        this.comment = comment;
        this.fqdns = fqdns;
        this.ips = ips;
        this.protocol_type_port = protocol_type_port;
        this.tsa_expiration_date = tsa_expiration_date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rlst rlst = (Rlst) o;
        return app_id.equals(rlst.app_id) && Objects.equals(change_type, rlst.change_type) && ips.equals(rlst.ips) && Objects.equals(tsa_expiration_date, rlst.tsa_expiration_date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(app_id, change_type, ips, tsa_expiration_date);
    }

    @Override
    public String toString() {
        return "Rlst{" +
                "app_id='" + app_id + '\'' +

                ", ips='" + ips + '\'' +
                '}';
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getApplication_name() {
        return application_name;
    }

    public void setApplication_name(String application_name) {
        this.application_name = application_name;
    }

    public String getChange_type() {
        return change_type;
    }

    public void setChange_type(String change_type) {
        this.change_type = change_type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFqdns() {
        return fqdns;
    }

    public void setFqdns(String fqdns) {
        this.fqdns = fqdns;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getIps() {
        return ips;
    }

    public void setIps(String ips) {
        this.ips = ips;
    }

    public String getProtocol_type_port() {
        return protocol_type_port;
    }

    public void setProtocol_type_port(String protocol_type_port) {
        this.protocol_type_port = protocol_type_port;
    }

    public String getTsa_expiration_date() {
        return tsa_expiration_date;
    }

    public void setTsa_expiration_date(String tsa_expiration_date) {
        this.tsa_expiration_date = tsa_expiration_date;
    }
}
