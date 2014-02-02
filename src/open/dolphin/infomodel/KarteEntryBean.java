package open.dolphin.infomodel;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * KarteEntry
 *
 * @author Minagawa,Kazushi
 *
 */
@MappedSuperclass
public class KarteEntryBean extends InfoModel implements Comparable {
    
    private static final long serialVersionUID = -9126237924533456842L;
    
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    
    @Column(nullable=false)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date confirmed;
    
    @Column(nullable=false)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date started;
    
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date ended;
    
    @Column(nullable=false)
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date recorded;
    
    private long linkId;
    
    private String linkRelation;
    
    @Column(length=1, nullable=false)
    private String status;
    
//    @Version
//    private int version;
    
    @ManyToOne
    @JoinColumn(name="creator_id", nullable=false)
    private UserModel creator;
    
    @ManyToOne
    @JoinColumn(name="karte_id", nullable=false)
    private KarteBean karte;
    
    
    /**
     * このエントリのIdを返す。
     * @return Id
     */
    public long getId() {
        return id;
    }
    
    /**
     * このエントリのIdを設定する。
     * @param id エントリId
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * 確定日時を返す。
     * @return 確定日時
     */
    public Date getConfirmed() {
        return confirmed;
    }

    /**
     * 確定日時を設定する。
     * @param confirmed 確定日時
     */
    public void setConfirmed(Date confirmed) {
        this.confirmed = confirmed;
    }
    
    /**
     * 適合開始日を返す。
     * @return 記録の適合開始日(TimeStamp)
     */    
    public Date getStarted() {
        return started;
    }

    /**
     * 適合開始日を設定する。
     * @param started 記録の適合開始日(TimeStamp)
     */
    public void setStarted(Date started) {
        this.started = started;
    }

    /**
     * 適合終了日を返す。
     * @return この記録の適合終了日時
     */
    public Date getEnded() {
        return ended;
    }

    /**
     * 適合終了日を設定する。
     * @param ended この記録の適合終了日時
     */
    public void setEnded(Date ended) {
        this.ended = ended;
    }

    /**
     * 記録日を返す。
     * @return このエントリの記録日時
     */
    public Date getRecorded() {
        return recorded;
    }

    /**
     * 記録日を設定する。
     * @param recorded このエントリの記録日時
     */
    public void setRecorded(Date recorded) {
        this.recorded = recorded;
    }
    
    /**
     * エントリのリンク先IDを返す。
     * @return エントリのリンク先ID
     */
    public long getLinkId() {
        return linkId;
    }

    /**
     * エントリのリンク先IDを設定する。
     * @param linkId エントリのリンク先ID
     */
    public void setLinkId(long linkId) {
        this.linkId = linkId;
    }
    
    /**
     * リンク先との関係を返す。
     * @return リンク先との関係
     */
    public String getLinkRelation() {
        return linkRelation;
    }

    /**
     * リンク先との関係を設定する。
     * @param linkRelation リンク先との関係
     */
    public void setLinkRelation(String linkRelation) {
        this.linkRelation = linkRelation;
    }
        
    /**
     * このエントリのステータスを返す。
     * @return ステータス
     */
    public String getStatus() {
        return status;
    }

    /**
     * このエントリのステータスを設定する。
     * @param status ステータス
     */
    public void setStatus(String status) {
        this.status = status;
    }

//    /**
//     * Version を返す。
//     * @return Version 
//     */
//    public int getVersion() {
//        return version;
//    }
//
//    /**
//     * Versionを設定する。
//     * @param Version
//     */
//    public void setVersion(int version) {
//        this.version = version;
//    }
    
    /**
     * Creatorを返す。
     * @return Creator (システムのユーザ)
     */
    public UserModel getCreator() {
        return creator;
    }
    
    /**
     * Creator を設定する。
     * @param creator このエントリの記述者
     */
    public void setCreator(UserModel creator) {
        this.creator = creator;
    }
    
    /**
     * カルテを返す。
     * @return Karte
     */
    public KarteBean getKarte() {
        return karte;
    }
    
    /**
     * カルテを設定する。
     * @param karte Karte
     */
    public void setKarte(KarteBean karte) {
        this.karte = karte;
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (int) (id ^ (id >>> 32));
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final KarteEntryBean other = (KarteEntryBean) obj;
        if (id != other.id)
            return false;
        return true;
    }
    
    /**
     * 適合開始日と確定日で比較する。
     * @return Comparable の比較値
     */    
    public int compareTo(Object other) {
        if (other != null && getClass() == other.getClass()) {
            Date date1 = getStarted();
            Date date2 = ((KarteEntryBean) other).getStarted();
            int result = compareDate(date1, date2);
            if (result == 0) {
                date1 = getConfirmed();
                date2 = ((KarteEntryBean) other).getConfirmed();
                result = compareDate(date1, date2);
            }
            return result;
        }
        return -1;
    }
    
    private int compareDate(Date date1, Date date2) {
        if (date1 != null && date2 == null) {
            return -1;
        } else if (date1 == null && date2 != null) {
            return 1;
        } else if (date1 == null && date2 == null) {
            return 0;
        } else {
            return date1.compareTo(date2);
        }
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
    
    
    //
    // 互換性用のプロキシコード
    //
    public Date getFirstConfirmed() {
        return getStarted();
    }
    
    public void setFirstConfirmed(Date firstConfirmed) {
        setStarted(firstConfirmed);
    }
    
    public String getFirstConfirmDate() {
        return ModelUtils.getDateTimeAsString(getFirstConfirmed());
    }
    
    public void setFirstConfirmDate(String timeStamp) {
        setFirstConfirmed(ModelUtils.getDateTimeAsObject(timeStamp));
    }
    
    public String getConfirmDate() {
        return ModelUtils.getDateTimeAsString(getConfirmed());
    }
    
    public void setConfirmDate(String timeStamp) {
        setConfirmed(ModelUtils.getDateTimeAsObject(timeStamp));
    }
    
      
    //
    // 足場コード  Date
    //
    public String firstConfirmDateAsString() {
        return dateAsString(getFirstConfirmed());
    }
    
    public String confirmDateAsString() {
        return dateAsString(getConfirmed());
    }
    
    public String startedDateAsString() {
        return dateAsString(getStarted());
    }
    
    public String endedDateAsString() {
        return dateAsString(getEnded());
    }
    
    public String recordedDateAsString() {
        return dateAsString(getRecorded());
    }
    
    private String dateAsString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_WITHOUT_TIME);
        return sdf.format(date);
    }
    
    //
    // 足場コード  TimeStamp
    //
    public String confirmedTimeStampAsString() {
        return timeStampAsString(getConfirmed());
    }
    
    public String startedTimeStampAsString() {
        return timeStampAsString(getStarted());
    }
    
    public String endedTimeStampAsString() {
        return timeStampAsString(getEnded());
    }
    
    public String recordedTimeStampAsString() {
        return timeStampAsString(getRecorded());
    }
    
    private String timeStampAsString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_8601_DATE_FORMAT);
        return sdf.format(date);
    }   
}


