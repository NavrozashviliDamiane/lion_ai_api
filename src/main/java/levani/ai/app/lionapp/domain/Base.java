package levani.ai.app.lionapp.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "base", schema = "vps_liontest_db")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usa_geo", nullable = false)
    private Boolean usaGeo;

    @Column(name = "warehouse", nullable = false)
    private Boolean warehouse;

    @Column(name = "author", nullable = false)
    private Integer author;

    @Column(name = "share_item")
    private String shareItem;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "point_of_delivery", nullable = false)
    private Integer pointOfDelivery;

    @Column(name = "amount_set_auction")
    private LocalDate amountSetAuction;

    @Column(name = "yes_no_filter_1", nullable = false)
    private Boolean yesNoFilter1;

    @Column(name = "pick_up_deadline")
    private LocalDate pickUpDeadline;

    @Column(name = "pick_up_date")
    private LocalDate pickUpDate;

    @Column(name = "yes_no_filter_2", nullable = false)
    private Boolean yesNoFilter2;

    @Column(name = "expect_pick_up")
    private LocalDate expectPickUp;

    @Column(name = "expect_delivery")
    private LocalDate expectDelivery;

    @Column(name = "canceling", nullable = false)
    private Boolean canceling;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "ready_for_dispatch")
    private Boolean readyForDispatch;

    @Column(name = "dispatch_status")
    private String dispatchStatus;

    @Column(name = "carrier")
    private String carrier;

    @Column(name = "carrier_phone")
    private String carrierPhone;

    @Column(name = "parts", nullable = false, columnDefinition = "TEXT")
    private String parts;

    @Column(name = "buyer_id", nullable = false)
    private String buyerId;

    @Column(name = "manufacturer", nullable = false)
    private Integer manufacturer;

    @Column(name = "model", nullable = false)
    private Integer model;

    @Column(name = "year", nullable = false)
    private String year;

    @Column(name = "vin")
    private String vin;

    @Column(name = "stock_lot")
    private String stockLot;

    @Column(name = "state", nullable = false)
    private Integer state;

    @Column(name = "zip_number")
    private String zipNumber;

    @Column(name = "pick_up_location")
    private String pickUpLocation;

    @Column(name = "auction_telephone")
    private String auctionTelephone;

    @Column(name = "auction_title")
    private Integer auctionTitle;

    @Column(name = "branch", nullable = false)
    private String branch;

    @Column(name = "personal_manager")
    private Integer personalManager;

    @Column(name = "bank_account_name")
    private Integer bankAccountName;

    @Column(name = "receiver_company")
    private Integer receiverCompany;

    @Column(name = "agent_company")
    private Integer agentCompany;

    @Column(name = "name_surname")
    private String nameSurname;

    @Column(name = "passport_number", nullable = false)
    private String passportNumber;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "diler_comment", nullable = false, columnDefinition = "TEXT")
    private String dilerComment;

    @Column(name = "auction_pay", nullable = false)
    private Double auctionPay;

    @Column(name = "deposit", nullable = false)
    private Double deposit;

    @Column(name = "auction_real_pay", nullable = false)
    private Double auctionRealPay;

    @Column(name = "auction_real_pay_date", nullable = false)
    private LocalDate auctionRealPayDate;

    @Column(name = "bill_to")
    private String billTo;

    @Column(name = "tax")
    private String tax;

    @Column(name = "requesting")
    private LocalDate requesting;

    @Column(name = "refunded")
    private LocalDate refunded;

    @Column(name = "exporter_company")
    private Boolean exporterCompany;

    @Column(name = "exp_status")
    private String expStatus;

    @Column(name = "title_of_state")
    private Integer titleOfState;

    @Column(name = "title_number")
    private String titleNumber;

    @Column(name = "car_location", columnDefinition = "TEXT")
    private String carLocation;

    @Column(name = "weight")
    private String weight;

    @Column(name = "title_sent")
    private LocalDate titleSent;

    @Column(name = "yes_no_filter_3", nullable = false)
    private Boolean yesNoFilter3;

    @Column(name = "title_accompanied")
    private LocalDate titleAccompanied;

    @Column(name = "deliver_date")
    private LocalDate deliverDate;

    @Column(name = "key_received")
    private LocalDate keyReceived;

    @Column(name = "user_comment", nullable = false, columnDefinition = "TEXT")
    private String userComment;

    @Column(name = "usa_paid")
    private String usaPaid;

    @Column(name = "pft_comment", nullable = false, columnDefinition = "TEXT")
    private String pftComment;

    @Column(name = "pft_comment_date")
    private LocalDate pftCommentDate;

    @Column(name = "container_number")
    private String containerNumber;

    @Column(name = "container_status", nullable = false, columnDefinition = "TEXT")
    private String containerStatus;

    @Column(name = "booking_number", nullable = false)
    private String bookingNumber;

    @Column(name = "modified_timestamp", nullable = false)
    private java.time.LocalDateTime modifiedTimestamp;

    @Transient
    private String manufacturerName;

    @Transient
    private String modelName;
}
