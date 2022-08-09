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

    @Column(name = "ACP Level" )
    private String acp_level;
    @Column(name = "APP ID" )
    private String app_id;
    @Column(name = "Application Name" )
    private String application_name;
    @Column(name = "Application Requester" )
    private String application_requester;
    @Column(name = "approved_by" )
    private String approved_by;
    @Column(name = "Change Type" )
    private String change_type;
    @Column(name = "Comment" )
    private String comment;
    @Column(name = "FQDN" )
    private String fqdn;
    @Column(name = "FQDNs" )
    private String fqdns;
    @Column(name = "IPs" )
    private String ips;
    @Column(name = "Last modified by Version" )
    private String last_modified_by_version;
    @Column(name = "Protocol type port" )
    private String protocol_type_port;
    @Column(name = "requested by" )
    private String request_by;
    @Column(name = "Source" )
    private String source;
    @Column(name = "TSA expiration date" )
    private String tsa_expiration_date;
    @Column(name = "Tufin ID" )
    private String tufin_id;

    public Rlst() {
    }

    public Rlst(String acp_level, String app_id, String application_name, String application_requester, String approved_by, String change_type, String comment, String fqdn, String fqdns, String ips, String last_modified_by_version, String protocol_type_port, String request_by, String source, String tsa_expiration_date, String tufin_id) {
        this.acp_level = acp_level;
        this.app_id = app_id;
        this.application_name = application_name;
        this.application_requester = application_requester;
        this.approved_by = approved_by;
        this.change_type = change_type;
        this.comment = comment;
        this.fqdn = fqdn;
        this.fqdns = fqdns;
        this.ips = ips;
        this.last_modified_by_version = last_modified_by_version;
        this.protocol_type_port = protocol_type_port;
        this.request_by = request_by;
        this.source = source;
        this.tsa_expiration_date = tsa_expiration_date;
        this.tufin_id = tufin_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rlst rlst = (Rlst) o;
        return app_id.equals(rlst.app_id) && Objects.equals(application_name, rlst.application_name) && Objects.equals(change_type, rlst.change_type) && Objects.equals(fqdn, rlst.fqdn) && ips.equals(rlst.ips) && Objects.equals(tsa_expiration_date, rlst.tsa_expiration_date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(app_id, application_name, change_type, fqdn, ips, tsa_expiration_date);
    }

    @Override
    public String toString() {
        return "Rlst{" +
                "app_id='" + app_id + '\'' +
                ", application_name='" + application_name + '\'' +
                ", ips='" + ips + '\'' +
                '}';
    }


    public String getAcp_level() {
        return acp_level;
    }

    public void setAcp_level(String acp_level) {
        this.acp_level = acp_level;
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

    public String getApplication_requester() {
        return application_requester;
    }

    public void setApplication_requester(String application_requester) {
        this.application_requester = application_requester;
    }

    public String getApproved_by() {
        return approved_by;
    }

    public void setApproved_by(String approved_by) {
        this.approved_by = approved_by;
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

    public String getFqdn() {
        return fqdn;
    }

    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
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

    public String getLast_modified_by_version() {
        return last_modified_by_version;
    }

    public void setLast_modified_by_version(String last_modified_by_version) {
        this.last_modified_by_version = last_modified_by_version;
    }

    public String getProtocol_type_port() {
        return protocol_type_port;
    }

    public void setProtocol_type_port(String protocol_type_port) {
        this.protocol_type_port = protocol_type_port;
    }

    public String getRequest_by() {
        return request_by;
    }

    public void setRequest_by(String request_by) {
        this.request_by = request_by;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTsa_expiration_date() {
        return tsa_expiration_date;
    }

    public void setTsa_expiration_date(String tsa_expiration_date) {
        this.tsa_expiration_date = tsa_expiration_date;
    }

    public String getTufin_id() {
        return tufin_id;
    }

    public void setTufin_id(String tufin_id) {
        this.tufin_id = tufin_id;
    }
}
