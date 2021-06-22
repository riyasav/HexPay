package io.thoughtbox.hamdan.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfileResponseData {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("fname")
    @Expose
    private String fname;
    @SerializedName("mname")
    @Expose
    private String mname;
    @SerializedName("lname")
    @Expose
    private String lname;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("sex")
    @Expose
    private String sex;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("nationality")
    @Expose
    private String nationality;
    @SerializedName("profession")
    @Expose
    private String profession;

    @SerializedName("professionid")
    @Expose
    private String professionid;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("salary")
    @Expose
    private String salary;
    @SerializedName("iscorporate")
    @Expose
    private String iscorporate;
    @SerializedName("tire")
    @Expose
    private String tire;
    @SerializedName("idtype")
    @Expose
    private String idtype;
    @SerializedName("idno")
    @Expose
    private String idno;
    @SerializedName("idissuecountry")
    @Expose
    private String idissuecountry;
    @SerializedName("idissue")
    @Expose
    private String idissue;
    @SerializedName("idexpiry")
    @Expose
    private String idexpiry;
    @SerializedName("branch")
    @Expose
    private String branch;
    @SerializedName("isonline")
    @Expose
    private String isonline;
    @SerializedName("isactivates")
    @Expose
    private String isactivates;
    @SerializedName("privilegegroup")
    @Expose
    private String privilegegroup;
    @SerializedName("passportno")
    @Expose
    private String passportno;
    @SerializedName("passportexpiry")
    @Expose
    private String passportexpiry;
    @SerializedName("countryofbirth")
    @Expose
    private String countryofbirth;

    @SerializedName("employer")
    @Expose
    private String employer;

    @SerializedName("employertype")
    @Expose
    private String employertype;


    @SerializedName("issmsenabled")
    @Expose
    private String issmsenabled;

    @SerializedName("isemailenabled")
    @Expose
    private String isemailenabled;

    @SerializedName("isphoneenabled")
    @Expose
    private String isphoneenabled;

    @SerializedName("iswhatsappenabled")
    @Expose
    private String iswhatsappenabled;


    @SerializedName("employertypeid")
    @Expose
    private String employertypeid;

    @SerializedName("videofile")
    @Expose
    private String videofile;
    @SerializedName("signaturefile")
    @Expose
    private String signaturefile;
    @SerializedName("idfiles")
    @Expose
    private List<Idfile> idfiles = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getIscorporate() {
        return iscorporate;
    }

    public void setIscorporate(String iscorporate) {
        this.iscorporate = iscorporate;
    }

    public String getTire() {
        return tire;
    }

    public void setTire(String tire) {
        this.tire = tire;
    }

    public String getIdtype() {
        return idtype;
    }

    public void setIdtype(String idtype) {
        this.idtype = idtype;
    }

    public String getIdno() {
        return idno;
    }

    public void setIdno(String idno) {
        this.idno = idno;
    }

    public String getIdissuecountry() {
        return idissuecountry;
    }

    public void setIdissuecountry(String idissuecountry) {
        this.idissuecountry = idissuecountry;
    }

    public String getIdissue() {
        return idissue;
    }

    public void setIdissue(String idissue) {
        this.idissue = idissue;
    }

    public String getIdexpiry() {
        return idexpiry;
    }

    public void setIdexpiry(String idexpiry) {
        this.idexpiry = idexpiry;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getIsonline() {
        return isonline;
    }

    public void setIsonline(String isonline) {
        this.isonline = isonline;
    }

    public String getIsactivates() {
        return isactivates;
    }

    public void setIsactivates(String isactivates) {
        this.isactivates = isactivates;
    }

    public String getPrivilegegroup() {
        return privilegegroup;
    }

    public void setPrivilegegroup(String privilegegroup) {
        this.privilegegroup = privilegegroup;
    }

    public String getPassportno() {
        return passportno;
    }

    public void setPassportno(String passportno) {
        this.passportno = passportno;
    }

    public String getPassportexpiry() {
        return passportexpiry;
    }

    public void setPassportexpiry(String passportexpiry) {
        this.passportexpiry = passportexpiry;
    }

    public String getCountryofbirth() {
        return countryofbirth;
    }

    public void setCountryofbirth(String countryofbirth) {
        this.countryofbirth = countryofbirth;
    }

    public String getEmployertype() {
        return employertype;
    }

    public void setEmployertype(String employertype) {
        this.employertype = employertype;
    }

    public String getVideofile() {
        return videofile;
    }

    public void setVideofile(String videofile) {
        this.videofile = videofile;
    }

    public String getSignaturefile() {
        return signaturefile;
    }

    public void setSignaturefile(String signaturefile) {
        this.signaturefile = signaturefile;
    }

    public List<Idfile> getIdfiles() {
        return idfiles;
    }

    public void setIdfiles(List<Idfile> idfiles) {
        this.idfiles = idfiles;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public String getProfessionid() {
        return professionid;
    }

    public void setProfessionid(String professionid) {
        this.professionid = professionid;
    }

    public String getEmployertypeid() {
        return employertypeid;
    }

    public void setEmployertypeid(String employertypeid) {
        this.employertypeid = employertypeid;
    }

    public String getIssmsenabled() {
        return issmsenabled;
    }

    public void setIssmsenabled(String issmsenabled) {
        this.issmsenabled = issmsenabled;
    }

    public String getIsemailenabled() {
        return isemailenabled;
    }

    public void setIsemailenabled(String isemailenabled) {
        this.isemailenabled = isemailenabled;
    }

    public String getIsphoneenabled() {
        return isphoneenabled;
    }

    public void setIsphoneenabled(String isphoneenabled) {
        this.isphoneenabled = isphoneenabled;
    }

    public String getIswhatsappenabled() {
        return iswhatsappenabled;
    }

    public void setIswhatsappenabled(String iswhatsappenabled) {
        this.iswhatsappenabled = iswhatsappenabled;
    }
}
