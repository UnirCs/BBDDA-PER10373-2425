# NOTES

## Carga de datos desde CSV

Using the following prompt to generate the CSV file.

```
Generate a csv file with headers and 20 rows.
The first column is 4 characters long, the first character is always a "d" and sequential numbers starting with d010, the next one would be d011, etc; the column header name is "dept_no".
The second column is 40 characters long with realistic company department names, the column header name is "dept_name".
```

Result:
```csv
dept_no,dept_name
d010,Human Resources - Head Office
d011,Accounting & Finance - New York
d012,Legal Department - Toronto
d013,Marketing & Sales - Los Angeles
d014,IT Support & Development - Vancouver
d015,Operations & Logistics - Chicago
d016,Executive Management - San Francisco
d017,Research & Development - Houston
d018,Customer Service - Dallas
d019,Public Relations - Miami
d020,International Sales - London
d021,Administrative Services - Berlin
d022,Strategic Planning - Paris
d023,Production - Beijing
d024,Quality Assurance - Tokyo
d025,Human Resources - Sydney
d026,Finance & Accounting - Toronto 2
d027,Legal Assistants - Miami Dade
d028,Sales & Trading - London City
d029,IT Operations - Bangalore
d030,Customer Experience - Taipei
d031,Marketing Analytics - Buenos Aires
d032,Local Operations - Moscow
d033,New Business Development - Johannesburg
```

Second prompt

```
Generate a CSV file with headers and 100 rows.
The first column is "emp_no" with sequential integers starting on 500000.
The second column is "birth_date" with dates in this format yyyy-MM-dd. The date should be at least 20 years in the past.
The third column is "first_name" with a maximum of 14 characters. It contains a random first name.
The forth column is "last_name" with a maximum of 16 characters. It contains a random last name.
The fith column is "gender" with value "M" for male and "F" for female.
The sixth column is "hire_date" with dates in this format yyyy-MM-dd. The date needs to be no more than 15 years in the past.
```

Result:
```csv
emp_no,birth_date,first_name,last_name,gender,hire_date
500000,1982-09-10,Alice,Johnson,F,2008-03-15
500001,1968-11-25,Bob,Williams,M,2006-06-20
500002,1975-03-30,Carol,Smith,F,2010-01-10
500003,1959-08-05,David,Lee,M,2007-07-25
500004,1988-06-12,Emma,Williams,F,2013-09-03
500005,1971-12-28,Frank,Brown,M,2011-11-02
500006,1985-02-19,Grace,Jones,F,2012-12-18
500007,1964-10-02,Henry,Miller,M,2007-02-13
500008,1983-04-23,Ivy,Martinez,F,2014-06-09
500009,1978-09-08,Jack,Wilson,M,2010-05-17
500010,1980-01-15,Jane,Taylor,F,2013-03-26
500011,1962-07-29,Kevin,Davis,M,2009-08-03
500012,1986-11-03,Lilly,Wilson,F,2014-11-11
500013,1973-06-17,Michael,Johnson,M,2011-02-08
500014,1981-03-03,Olivia,Davis,F,2012-04-23
500015,1976-08-21,Paul,Miller,M,2010-09-13
500016,1987-12-02,Queen,Taylor,F,2015-05-27
500017,1967-05-14,Robert,Martinez,M,2008-08-06
500018,1984-07-01,Sarah,Williams,F,2013-12-16
500019,1970-10-23,Thomas,Brown,M,2007-11-07
500020,1989-05-19,Victoria,Miller,F,2014-02-18
500021,1966-01-10,William,Wilson,M,2006-04-25
500022,1987-09-02,Xavier,Taylor,M,2015-03-09
500023,1983-12-17,Yvonne,Martinez,F,2012-07-16
500024,1960-05-07,Arthur,Williams,M,2006-09-12
500025,1985-07-23,Barbara,Brown,F,2014-04-29
500026,1974-02-11,Charles,Miller,M,2011-06-01
500027,1982-11-06,Doris,Jones,F,2013-06-11
500028,1969-06-03,Edward,Lee,M,2007-05-06
500029,1980-02-14,Ellen,Martinez,F,2011-09-05
500030,1972-08-26,Franklyn,Wilson,M,2009-02-10
500031,1988-09-29,Graceful,Taylor,F,2015-08-24
500032,1963-03-13,Henryk,Miller,M,2006-06-19
500033,1981-05-12,Ivylyn,Martinez,F,2012-11-06
500034,1977-09-04,Jacklyn,Williams,F,2010-07-13
500035,1965-12-30,Kevin,Johnson,M,2007-09-03
500036,1983-07-16,Leta,Brown,F,2013-05-07
500037,1970-04-28,Michael,Miller,M,2009-01-12
500038,1986-04-03,Olivia,Taylor,F,2014-08-05
500039,1961-01-08,Peter,Wilson,M,2006-08-01
500040,1984-10-06,Queen,Martinez,F,2013-02-12
500041,1973-03-21,Robert,Williams,M,2011-05-18
500042,1987-06-14,Sarah,Johnson,F,2015-02-03
500043,1968-08-09,Thomas,Miller,M,2008-02-11
500044,1982-02-20,Victoria,Williams,F,2014-11-25
500045,1975-11-09,William,Johnson,M,2010-03-16
500046,1988-08-15,Xavier,Martinez,M,2015-06-09
500047,1967-02-26,Yvonne,Williams,F,2008-04-02
500048,1985-04-29,Albert,Brown,M,2014-02-03
500049,1980-08-19,Amelia,Miller,F,2012-06-11
500050,1978-05-01,Benjamin,Williams,M,2011-01-03
500051,1986-10-07,Carol,Martinez,F,2015-01-06
500052,1965-06-17,David,Miller,M,2007-01-09
500053,1984-01-10,Eva,Williams,F,2013-09-16
500054,1972-11-02,Frank,Martinez,M,2009-05-04
500055,1987-07-08,Grace,Miller,F,2015-05-12
500056,1969-03-25,Henry,Williams,M,2008-07-01
500057,1982-09-04,Ivy,Martinez,F,2014-07-15
500058,1976-12-14,Jack,Miller,M,2011-09-01
500059,1980-06-02,Jane,Williams,F,2013-02-25
500060,1974-08-06,Kevin,Martinez,M,2010-11-02
500061,1985-11-12,Lilly,Miller,F,2014-05-19
500062,1967-05-21,Michael,Williams,M,2007-03-13
500063,1988-03-03,Olivia,Martinez,F,2015-09-08
500064,1971-07-17,Paul,Miller,M,2009-09-02
500065,1983-05-27,Queen,Williams,F,2014-01-07
500066,1975-09-09,Robert,Martinez,M,2011-03-22
500067,1986-06-23,Sarah,Miller,F,2015-03-03
500068,1963-08-13,Thomas,Williams,M,2007-02-05
500069,1981-12-04,Victoria,Martinez,F,2013-07-14
500070,1979-04-16,William,Miller,M,2010-06-08
500071,1987-08-20,Xavier,Williams,M,2015-02-11
500072,1966-02-24,Yvonne,Martinez,F,2008-01-15
500073,1985-06-03,Albert,Miller,M,2014-09-02
500074,1989-11-18,Amelia,Williams,F,2015-07-13
500075,1973-07-03,Benjamin,Martinez,M,2011-08-09
500076,1983-02-12,Carol,Miller,F,2014-01-20
500077,1960-09-01,David,Williams,M,2006-11-03
500078,1986-08-26,Eva,Martinez,F,2015-01-01
500079,1977-03-19,Frank,Miller,M,2010-02-01
500080,1981-07-06,Grace,Williams,F,2013-03-11
500081,1985-07-03,Linda,Miller,F,2012-11-26
500082,1966-11-29,James,Williams,M,2007-02-05
500083,1975-04-06,Sarah,Miller,F,2009-01-05
500084,1983-09-13,Michael,Jackson,M,2011-06-01
500085,1987-06-02,Emma,Williams,F,2013-03-04
500086,1965-01-16,Robert,Miller,M,2006-09-04
500087,1992-12-29,David,Jackson,M,2014-05-01
500088,1970-09-01,Jessica,Williams,F,2010-08-09
500089,1978-02-12,William,Miller,M,2009-11-09
500090,1982-05-01,Linda,Jackson,F,2012-05-27
500091,1963-09-09,James,Williams,M,2007-01-01
500092,1979-03-03,Sarah,Miller,F,2009-08-03
500093,1988-07-29,Michael,Jackson,M,2012-09-03
500094,1985-04-10,Emma,Williams,F,2014-01-06
500095,1961-01-03,Robert,Miller,M,2006-07-03
500096,1997-06-23,David,Jackson,M,2015-11-02
500097,1972-12-07,Jessica,Williams,F,2010-05-01
500098,1976-06-16,William,Miller,M,2009-06-01
500099,1980-11-09,Linda,Jackson,F,2013-01-01
```