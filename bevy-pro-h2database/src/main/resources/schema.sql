-- Beverage dispenser schema.

-- Beverage table; represents a beverage brand
CREATE TABLE beverage(
	id integer not null identity(0,1) primary key,
	image_url varchar(255) not null,
	name varchar(50) not null,
	unit_price integer not null
);

-- Beverage Slot table; maps a beverage to a hardware slot
CREATE TABLE beverage_slot(
	id integer not null identity(0,1) primary key,
	beverage_id integer not null,
	slot_number integer not null unique,
	
	foreign key(beverage_id) references beverage(id)
);

-- Purchase table; tracks purchase history. Beverage foreign key is not used, 
--   because beverage name and purchase price may change.
CREATE TABLE purchase(
	id integer not null identity(0,1) primary key,
	purchase_time timestamp not null default now(),
	beverage_name varchar(50) not null,	
-- Purchase Amount: How many units were purchased (for liquid dispensers; for cans/bottles, this is always 1)
	purchase_amount integer not null, 
	purchase_price integer not null,
);