create table vps_liontest_db.action_desc
(
    rid     int auto_increment
        primary key,
    base_id int                         not null,
    actsc   varchar(255)                not null,
    price   double(15, 3) default 0.000 not null
)
    collate = utf8mb3_unicode_ci;

create index base_id
    on vps_liontest_db.action_desc (base_id);

create table vps_liontest_db.agent_company
(
    id    tinyint(2) auto_increment
        primary key,
    title varchar(30) collate utf8mb3_unicode_ci not null,
    info  text                                   not null
)
    charset = utf8mb3;

create index id
    on vps_liontest_db.agent_company (id);

create table vps_liontest_db.auction_title
(
    id     tinyint(2) auto_increment
        primary key,
    status tinyint(1) default 1                   not null,
    title  varchar(30) collate utf8mb3_unicode_ci not null
)
    charset = utf8mb3;

create index id
    on vps_liontest_db.auction_title (id);

create table vps_liontest_db.bank_account_name
(
    id     tinyint(2) auto_increment
        primary key,
    title  varchar(150) collate utf8mb3_unicode_ci not null,
    info   text                                    not null,
    status tinyint(1)                              not null
)
    charset = utf8mb3;

create index id
    on vps_liontest_db.bank_account_name (id);

create table vps_liontest_db.base
(
    id                           int unsigned auto_increment,
    usa_geo                      tinyint(1)                                                                                           not null,
    warehouse                    tinyint(1)                                                                                           not null,
    author                       int(3)                                                                                               not null,
    share_item                   varchar(255)                                                                                         null,
    date                         date                                                                                                 not null,
    point_of_delivery            int                                                                                                  not null,
    amount_set_auction           date                                                                                                 null comment '????? ????? ?????????',
    yes_no_filter_1              tinyint(1)    default 0                                                                              not null,
    pick_up_deadline             date                                                                                                 null,
    pick_up_date                 date                                                                                                 null,
    yes_no_filter_2              tinyint(1)    default 0                                                                              not null,
    expect_pick_up               date                                                                                                 null,
    expect_delivery              date                                                                                                 null,
    canceling                    tinyint(1)    default 0                                                                              not null,
    order_id                     varchar(200)                                                                                         null,
    ready_for_dispatch           tinyint(1)                                                                                           null,
    dispatch_status              enum ('New', 'On Site', 'Order Taken', 'Pickup', 'Deliver')                                          null,
    carrier                      varchar(100)                                                                                         null,
    carrier_phone                varchar(100)                                                                                         null,
    parts                        text                                                                                                 not null,
    buyer_id                     varchar(50)                                                                                          not null,
    manufacturer                 int(3)                                                                                               not null,
    model                        int(4)                                                                                               not null,
    year                         varchar(4)                                                                                           not null,
    vin                          varchar(30)                                                                                          null,
    stock_lot                    varchar(100)                                                                                         null,
    state                        int                                                                                                  not null,
    zip_number                   varchar(20)                                                                                          null comment 'ZIP ',
    pick_up_location             varchar(200)                                                                                         null,
    auction_telephone            varchar(100)                                                                                         null,
    auction_title                int                                                                                                  null,
    branch                       varchar(200)                                                                                         not null,
    personal_manager             int                                                                                                  null,
    bank_account_name            int                                                                                                  null,
    receiver_company             int                                                                                                  null,
    agent_company                int(2)                                                                                               null,
    name_surname                 varchar(255)                                                                                         null,
    passport_number              varchar(20)                                                                                          not null,
    telephone                    varchar(100)                                                                                         null,
    diler_comment                text                                                                                                 not null,
    auction_pay                  double(15, 2) default 0.00                                                                           not null,
    deposit                      double(15, 2) default 0.00                                                                           not null,
    auction_real_pay             double(15, 2) default 0.00                                                                           not null,
    auction_real_pay_date        date          default '0000-00-00'                                                                   not null,
    bill_to                      varchar(100)                                                                                         null,
    tax                          varchar(100)                                                                                         null,
    requesting                   date                                                                                                 null,
    refunded                     date                                                                                                 null,
    exporter_company             tinyint(1)                                                                                           null,
    exp_status                   varchar(100)                                                                                         null,
    title_of_state               int                                                                                                  null,
    title_number                 varchar(100)                                                                                         null,
    car_location                 text                                                                                                 null,
    weight                       varchar(100)                                                                                         null,
    title_sent                   date                                                                                                 null,
    yes_no_filter_3              tinyint(1)    default 0                                                                              not null,
    title_accompanied            date                                                                                                 null,
    deliver_date                 date                                                                                                 null,
    key_received                 date                                                                                                 null,
    user_comment                 text                                                                                                 not null,
    usa_paid                     varchar(100)                                                                                         null,
    pft_comment                  text                                                                                                 not null,
    pft_comment_date             date          default '0000-00-00'                                                                   null,
    cut_damage                   double(15, 2) default 0.00                                                                           not null,
    money_left_bank_date         date          default '0000-00-00'                                                                   null,
    price_for_towing             double(15, 2) default 0.00                                                                           not null,
    inside_transport_usa         double(15, 2) default 0.00                                                                           not null,
    container_company            double(15, 2) default 0.00                                                                           not null,
    container_amount             double(15, 2) default 0.00                                                                           not null,
    diler                        double(15, 2) default 0.00                                                                           not null,
    diler2                       double(15, 2) default 0.00                                                                           not null,
    diler3                       double(15, 2) default 0.00                                                                           not null,
    transfer_charges             double(15, 2) default 0.00                                                                           not null,
    late_payment                 double(15, 2) default 0.00                                                                           not null,
    load_fee                     double(15, 2) default 0.00                                                                           not null,
    storeg_fee                   double(15, 2) default 0.00                                                                           not null,
    funding                      double(15, 2) default 0.00                                                                           not null,
    funding_votes                double(15, 2) default 0.00                                                                           null,
    insurance                    double(15, 2) default 0.00                                                                           null,
    consolidation_fee            double(15, 2) default 0.00                                                                           not null,
    freight                      double(15, 4) default 0.0000                                                                         not null,
    dealer_f1                    double(15, 4) default 0.0000                                                                         not null,
    sale_pay                     double(15, 4) default 0.0000                                                                         not null,
    pm_1                         double(15, 2) default 0.00                                                                           not null,
    client_payer_1               varchar(255)                                                                                         null,
    payment_date_1               date          default '0000-00-00'                                                                   null,
    pm_2                         double(15, 2) default 0.00                                                                           not null,
    client_payer_2               varchar(255)                                                                                         null,
    payment_date_2               date          default '0000-00-00'                                                                   null,
    pm_3                         double(15, 2) default 0.00                                                                           not null,
    client_payer_3               varchar(255)                                                                                         null,
    payment_date_3               date          default '0000-00-00'                                                                   null,
    pm_4                         double(15, 2) default 0.00                                                                           not null,
    client_payer_4               varchar(255)                                                                                         null,
    payment_date_4               date          default '0000-00-00'                                                                   null,
    pm_5                         double(15, 2) default 0.00                                                                           not null,
    client_payer_5               varchar(255)                                                                                         null,
    payment_date_5               date          default '0000-00-00'                                                                   null,
    pm_6                         double(15, 2) default 0.00                                                                           not null,
    client_payer_6               varchar(255)                                                                                         null,
    payment_date_6               date          default '0000-00-00'                                                                   not null,
    pay_reset                    double(15, 2) default 0.00                                                                           not null,
    pay_comment                  text                                                                                                 not null,
    pay_date                     date          default '0000-00-00'                                                                   not null,
    pay_reset2                   double(15, 2) default 0.00                                                                           not null,
    pay_comment2                 text                                                                                                 not null,
    pay_date2                    date          default '0000-00-00'                                                                   not null,
    consolidation_pay            double(15, 2) default 0.00                                                                           not null,
    consolidation_date           date                                                                                                 null,
    remember_status              tinyint(1)                                                                                           null,
    remember_comment             text                                                                                                 not null,
    remember_date                date          default '0000-00-00'                                                                   null,
    pre_payments                 double(15, 2) default 0.00                                                                           not null,
    date_1                       date          default '0000-00-00'                                                                   null,
    green_invoice_1              varchar(20)                                                                                          null,
    transfer_money_2_stage       double(15, 2) default 0.00                                                                           not null,
    transfer_date_2_stage        date          default '0000-00-00'                                                                   not null,
    green_invoice_2              varchar(20)                                                                                          null,
    transfer_money_3_stage       double(15, 2) default 0.00                                                                           not null,
    transfer_date_3_stage        date          default '0000-00-00'                                                                   not null,
    transfer_money_4_stage       double(15, 2) default 0.00                                                                           not null,
    transfer_date_4_stage        date          default '0000-00-00'                                                                   not null,
    transfer_money_5_stage       double(15, 2) default 0.00                                                                           not null,
    transfer_date_5_stage        date          default '0000-00-00'                                                                   not null,
    transfer_money_6_stage       double(15, 2) default 0.00                                                                           not null,
    broker_info                  text                                                                                                 not null,
    transfer_date_6_stage        date          default '0000-00-00'                                                                   not null,
    transfer_money_8_stage       double(15, 2) default 0.00                                                                           not null,
    transfer_date_8_stage        date          default '0000-00-00'                                                                   not null,
    transfer_money_9_stage       double(15, 2) default 0.00                                                                           not null,
    transfer_date_9_stage        date          default '0000-00-00'                                                                   not null,
    yes_no_transfer_date_9_stage tinyint(1)    default 0                                                                              not null,
    transfer_money_7_stage       double(15, 2) default 0.00                                                                           not null,
    transfer_date_7_stage        date          default '0000-00-00'                                                                   not null,
    pickup_manager               int                                                                                                  null,
    pickup_manager2              int                                                                                                  null,
    already_contact              enum ('დარეკილია', 'ვერ დავუკავშირდი', 'არ არის დარეკილი', 'ხელახლა დაკავშირება', 'რეგულირი მძღოლი') null,
    refund_payments              double(15, 2) default 0.00                                                                           not null,
    refund_date                  date          default '0000-00-00'                                                                   null,
    refund_comment               text                                                                                                 not null,
    transfer_comment             text                                                                                                 not null,
    parts_document               text                                                                                                 not null,
    stop_loading                 text                                                                                                 not null,
    finance_problem              text                                                                                                 not null,
    parts_included               text                                                                                                 not null,
    container_line               int(1)                                                                                               not null,
    who_loaded                   tinyint(1)    default 0                                                                              not null,
    container_capacity           tinyint(1)                                                                                           null,
    where_comes                  int                                                                                                  not null,
    booking_number               varchar(50) charset latin1                                                                           not null,
    doc_cutoff                   date                                                                                                 null,
    master_sent                  date                                                                                                 null,
    valid_title_sent             date                                                                                                 null,
    ship_name                    varchar(50)                                                                                          null,
    container_number             varchar(50)                                                                                          null,
    container_status             text                                                                                                 not null,
    seal_number                  varchar(50)                                                                                          null,
    aes_number                   varchar(50)                                                                                          null,
    terminal_out_date            date                                                                                                 null,
    yes_no_terminal_out_date     tinyint(1)    default 0                                                                              not null,
    polygon                      int           default 0                                                                              not null,
    container_entry_date         date                                                                                                 null,
    yes_no_rs                    tinyint(1)                                                                                           null,
    date_open_container          date                                                                                                 null,
    date_of_output               date          default '0000-00-00'                                                                   not null,
    date_release                 date                                                                                                 null,
    bill_of_lading               date                                                                                                 null,
    title_posted                 date                                                                                                 null,
    title_received               date                                                                                                 null,
    title_issued                 date                                                                                                 null,
    in_site                      tinyint(1)    default 0                                                                              not null,
    in_archive                   datetime                                                                                             null comment '????? ?????? ? ??????',
    is_del                       datetime                                                                                             null comment '????? ?????? ? ???????',
    yes_no_filter_4              tinyint(1)    default 0                                                                              not null,
    yes_no_filter_5              tinyint(1)    default 0                                                                              not null,
    yes_no_filter_6              tinyint(1)    default 0                                                                              not null,
    yes_no_filter_bring          tinyint(1)    default 0                                                                              not null,
    yes_no_filter_7              tinyint(1)    default 0                                                                              not null,
    yes_no_filter_8              tinyint(1)    default 0                                                                              not null,
    diler_to_balance             double(15, 2) default 0.00                                                                           not null,
    diler_from_balance           double(15, 2) default 0.00                                                                           not null,
    modified_timestamp           timestamp     default current_timestamp()                                                            not null on update current_timestamp(),
    container_real_entry_date    date                                                                                                 null,
    title_status                 text                                                                                                 not null,
    doc_status                   varchar(255)                                                                                         null,
    auction_cities               varchar(100)                                                                                         null,
    driver_key_received          date                                                                                                 null,
    driver_title_received        date                                                                                                 null,
    primary key (id, usa_geo, author, container_line),
    constraint id_base
        unique (id)
)
    collate = utf8mb3_unicode_ci;

create index auction_pay_base
    on vps_liontest_db.base (auction_pay);

create index base_cont_num
    on vps_liontest_db.base (container_number);

create index date_base
    on vps_liontest_db.base (date);

create index ndx_base_author
    on vps_liontest_db.base (author);

create index ndx_buyer_id
    on vps_liontest_db.base (buyer_id);

create index ndx_share_item
    on vps_liontest_db.base (share_item);

create index usageo_base
    on vps_liontest_db.base (usa_geo);

create index vin_base
    on vps_liontest_db.base (vin);

create table vps_liontest_db.container_line
(
    id    tinyint(2) auto_increment
        primary key,
    title varchar(30) collate utf8mb3_unicode_ci not null
)
    charset = utf8mb3;

create table vps_liontest_db.damage_desc
(
    rid     int auto_increment,
    base_id int                         not null,
    dgsc    varchar(255)                not null,
    price   double(15, 3) default 0.000 not null,
    primary key (rid, base_id)
)
    collate = utf8mb3_unicode_ci;

create index ndx_damage_base_id
    on vps_liontest_db.damage_desc (base_id);

create table vps_liontest_db.damage_photos
(
    pid            int auto_increment
        primary key,
    damage_desc_id int          not null,
    filename       varchar(255) not null,
    fileext        varchar(5)   not null,
    size           int          not null,
    mime           varchar(100) not null,
    create_time    datetime     not null,
    width          int          not null,
    height         int          not null
)
    charset = utf8mb3;

create index damage_desc_id
    on vps_liontest_db.damage_photos (damage_desc_id);

create index pid
    on vps_liontest_db.damage_photos (pid);

create table vps_liontest_db.diler_desc
(
    rid     int auto_increment
        primary key,
    base_id int                         not null,
    dsc     varchar(255)                not null,
    price   double(15, 3) default 0.000 not null
)
    collate = utf8mb3_unicode_ci;

create index base_id
    on vps_liontest_db.diler_desc (base_id);

create table vps_liontest_db.exporter_company
(
    id     tinyint(2) auto_increment
        primary key,
    title  varchar(30) collate utf8mb3_unicode_ci not null,
    info   text                                   not null,
    status tinyint(1)                             not null
)
    charset = utf8mb3;

create index id
    on vps_liontest_db.exporter_company (id);

create table vps_liontest_db.manufacturer
(
    id   int(3) unsigned auto_increment,
    name varchar(64) not null,
    constraint id
        unique (id)
)
    charset = utf8mb3;

create index id_2
    on vps_liontest_db.manufacturer (id);

create table vps_liontest_db.model
(
    id           int(4) unsigned auto_increment,
    name         varchar(255) not null,
    manufacturer int(3)       null,
    constraint id
        unique (id)
)
    charset = utf8mb3;

create index id_2
    on vps_liontest_db.model (manufacturer);

create table vps_liontest_db.ottiko_desc
(
    rid     int auto_increment
        primary key,
    base_id int                         not null,
    ottosc  varchar(255)                not null,
    price   double(15, 3) default 0.000 not null
)
    collate = utf8mb3_unicode_ci;

create index base_id
    on vps_liontest_db.ottiko_desc (base_id);

create table vps_liontest_db.personal_manager
(
    id     tinyint(2) auto_increment
        primary key,
    title  varchar(150) collate utf8mb3_unicode_ci not null,
    info   text                                    not null,
    status tinyint(1)                              not null
)
    charset = utf8mb3;

create index id
    on vps_liontest_db.personal_manager (id);

create table vps_liontest_db.photos
(
    pid         int auto_increment
        primary key,
    base_id     int          not null,
    filename    varchar(255) not null,
    fileext     varchar(5)   not null,
    size        int          not null,
    mime        varchar(100) not null,
    create_time datetime     not null,
    width       int          not null,
    height      int          not null,
    location    tinyint(1)   not null,
    gan         int(1)       not null,
    cdn_fid     char(33)     null,
    url         varchar(255) null,
    constraint pid
        unique (pid)
)
    charset = utf8mb3;

create index base_id
    on vps_liontest_db.photos (base_id);

create table vps_liontest_db.photos_new
(
    pid         int auto_increment
        primary key,
    base_id     int          not null,
    filename    varchar(255) not null,
    fileext     varchar(5)   not null,
    size        int          not null,
    mime        varchar(100) not null,
    create_time datetime     not null,
    width       int          not null,
    height      int          not null,
    location    tinyint(1)   not null,
    gan         int(1)       not null
)
    charset = utf8mb3;

create table vps_liontest_db.pickup_manager
(
    id     tinyint(2) auto_increment
        primary key,
    title  varchar(150) collate utf8mb3_unicode_ci not null,
    info   text                                    not null,
    status tinyint(1)                              not null
)
    charset = utf8mb3;

create index id
    on vps_liontest_db.pickup_manager (id);

create table vps_liontest_db.point_of_delivery
(
    id    tinyint(1) auto_increment
        primary key,
    title varchar(30) collate utf8mb3_unicode_ci not null
)
    charset = utf8mb3;

create table vps_liontest_db.polygon
(
    id        int auto_increment
        primary key,
    status    tinyint(1)   not null,
    state     varchar(25)  not null,
    title     varchar(25)  not null,
    latitude  varchar(100) not null,
    longitude varchar(100) not null,
    capacity  int(5)       not null
)
    collate = utf8mb3_unicode_ci;

create table vps_liontest_db.receiver_company
(
    id     int auto_increment
        primary key,
    title  varchar(150) collate utf8mb3_unicode_ci not null,
    info   text                                    not null,
    status tinyint(1)                              not null
)
    charset = utf8mb3;

create index id
    on vps_liontest_db.receiver_company (id);

create table vps_liontest_db.remember_status
(
    id    tinyint(1) auto_increment
        primary key,
    title varchar(80) collate utf8mb3_unicode_ci not null
)
    charset = utf8mb3;

create table vps_liontest_db.state
(
    id    tinyint(2) auto_increment
        primary key,
    title varchar(2) collate utf8mb3_unicode_ci not null
)
    charset = utf8mb3;

create index id
    on vps_liontest_db.state (id);

create table vps_liontest_db.usa_geo
(
    id    tinyint(1) auto_increment
        primary key,
    title varchar(30) collate utf8mb3_unicode_ci not null
)
    charset = utf8mb3;

create index id
    on vps_liontest_db.usa_geo (id);

create table vps_liontest_db.users
(
    uid      int(2) unsigned auto_increment
        primary key,
    gid      int                        not null,
    name     varchar(255)               not null,
    login    varchar(64)                not null,
    email    varchar(100)               not null,
    status   tinyint(1)    default 0    not null,
    password varchar(64)                not null,
    old_name varchar(255)               not null,
    balance  double(15, 2) default 0.00 not null,
    ip       varchar(200)               not null,
    regdate  datetime                   null,
    e_email  varchar(255)               null,
    tel      varchar(255)               null,
    addr     varchar(255)               null,
    personID varchar(11)                null,
    cityid   int                        null,
    cityName varchar(255)               null,
    constraint uid_users
        unique (uid)
)
    collate = utf8mb3_unicode_ci;

create index cityid_users
    on vps_liontest_db.users (cityid);

create index gid_users
    on vps_liontest_db.users (gid);

create table vps_liontest_db.users_copy
(
    uid      int(2) unsigned auto_increment
        primary key,
    gid      int                        not null,
    name     varchar(255)               not null,
    login    varchar(64)                not null,
    email    varchar(100)               not null,
    status   tinyint(1)    default 0    not null,
    password varchar(64)                not null,
    old_name varchar(255)               not null,
    balance  double(15, 2) default 0.00 not null,
    ip       varchar(200)               not null,
    regdate  datetime                   null,
    e_email  varchar(255)               null,
    tel      varchar(255)               null,
    addr     varchar(255)               null,
    personID varchar(11)                null,
    cityid   int                        null,
    cityName varchar(255)               null,
    constraint uid_users
        unique (uid)
)
    collate = utf8mb3_unicode_ci;

create index cityid_users
    on vps_liontest_db.users_copy (cityid);

create index gid_users
    on vps_liontest_db.users_copy (gid);

create table vps_liontest_db.warehouse
(
    id    tinyint(1) auto_increment
        primary key,
    title varchar(30) not null
)
    collate = utf8mb3_unicode_ci;

create table vps_liontest_db.where_comes
(
    id    tinyint(1) auto_increment
        primary key,
    title varchar(30) collate utf8mb3_unicode_ci not null
)
    charset = utf8mb3;

create table vps_liontest_db.who_loaded
(
    id    tinyint(1) auto_increment
        primary key,
    title varchar(30) collate utf8mb3_unicode_ci not null
)
    charset = utf8mb3;

create table vps_liontest_db.yes_no_rs
(
    id    tinyint(1) auto_increment
        primary key,
    title varchar(30) collate utf8mb3_unicode_ci not null
)
    charset = utf8mb3;

