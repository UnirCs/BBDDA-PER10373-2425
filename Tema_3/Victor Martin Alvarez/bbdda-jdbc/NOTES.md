# NOTES

## Carga de datos desde CSV

Using the following prompt to generate the CSV file.

```
Generate a csv file with headers and 20 rows.
The first column is 4 characters long, the first character is always a "d" and sequential numbers starting with d008, the next one would be d009, etc; the column header name is "dept_no".
The second column is 40 characters long with realistic company department names, the column header name is "dept_name".
```

Result:
```csv
dept_no,dept_name
d008,Human Resources Department
d009,Accounting and Finance Division
d010,Marketing and Sales Office
d011,Information Technology Sector
d012,Executive Board Room
d013,Legal Services Team
d014,Customer Support Wing
d015,Medical Benefits Sector
d016,Payroll Division
d017,Operations Unit
d018,R&D Laboratory
d019,IT Help Desk Cell
d020,Public Relations Branch
d021,Security and Safety Division
d022,Legal Advisory Council
d023,Business Development Team
d024,HR Recruitment Office
d025,Finance and Audit Department
d026,Sales and Marketing Division
d027,IT Development Center
d028,Legal Operations Office
d029,Customer Experience Bureau
d030,Health and Wellness Committee
d031,Administrative Zone
d032,Main Framework Team
```

Second prompt

```
Generate a CSV file with headers and 100 rows.
The first column is "emp_no" with sequential integers starting on 20000.
The second column is "birth_date" with dates in this format yyyy-MM-dd. The date should be at least 20 years in the past.
The third column is "first_name" with a maximum of 14 characters. It contains a random first name.
The forth column is "last_name" with a maximum of 16 characters. It contains a random last name.
The fith column is "gender" with value "M" for male and "F" for female.
The sixth column is "hire_date" with dates in this format yyyy-MM-dd. The date needs to be no more than 15 years in the past.
```

Result:
```csv
emp_no,birth_date,first_name,last_name,gender,hire_date
20000,1981-07-16,Mary,Jackson,F,2009-03-11
20001,1958-03-29,John,Doe,M,2007-09-03
20002,1987-12-02,Emma,Williams,F,2013-06-24
20003,1965-06-12,Michael,Brown,M,2006-05-07
20004,1980-11-04,Sarah,Miller,F,2010-02-16
20005,1961-02-19,David,Lee,M,2005-12-18
20006,1996-09-06,Jessica,Wilson,F,2015-08-03
20007,1964-04-23,Robert,DeLaCruz,M,2008-07-02
20008,1988-09-15,William,Davis,M,2012-01-29
20009,1973-03-07,Linda,Gonzalez,F,2011-09-06
20010,1985-08-24,James,Miller,M,2014-07-21
20011,1967-12-13,Sarah,Miller,F,2007-01-14
20012,1999-05-10,Michael,Jackson,M,2016-11-22
20013,1977-02-03,Emma,Jones,F,2010-06-07
20014,1984-12-20,Robert,Brown,M,2013-03-31
20015,1971-07-06,Linda,Williams,F,2009-08-02
20016,1969-01-14,David,Miller,M,2006-02-19
20017,1992-11-25,Jessica,Davis,F,2014-12-01
20018,1975-06-09,William,Miller,M,2012-02-27
20019,1983-01-05,James,Williams,M,2011-04-12
20020,1963-03-18,Sarah,Williams,F,2007-08-06
20021,1986-08-14,Michael,Miller,M,2010-01-04
20022,1978-04-22,Emma,Brown,F,2009-02-03
20023,1960-12-04,Robert,Miller,M,2005-11-07
20024,1994-09-01,David,Lee,M,2015-03-23
20025,1987-02-11,Jessica,Jackson,F,2013-01-13
20026,1972-08-17,William,Williams,M,2011-08-09
20027,1982-11-02,Linda,Miller,F,2012-06-11
20028,1957-06-03,James,Doe,M,2006-01-16
20029,1991-03-13,Sarah,Lee,F,2014-07-07
20030,1976-12-21,Michael,Gonzalez,M,2008-01-27
20031,1985-05-19,Emma,Williams,F,2012-08-20
20032,1962-09-02,Robert,Brown,M,2007-06-11
20033,1974-01-10,David,Miller,M,2009-09-01
20034,1998-07-08,Jessica,Miller,F,2016-04-18
20035,1970-03-16,William,Jackson,M,2010-05-02
20036,1988-06-20,Linda,Williams,F,2014-02-10
20037,1966-11-08,James,Miller,M,2008-04-21
20038,1973-09-03,Sarah,Jackson,F,2009-08-17
20039,1982-02-18,Michael,Williams,M,2011-01-03
20040,1978-07-29,Emma,Miller,F,2010-03-08
20041,1965-01-06,Robert,Jackson,M,2006-08-01
20042,1989-12-31,David,Williams,M,2013-02-17
20043,1995-05-14,Jessica,Brown,F,2016-02-01
20044,1979-01-01,William,Miller,M,2012-08-06
20045,1981-09-19,Linda,Jackson,F,2011-02-07
20046,1968-05-09,James,Williams,M,2007-02-12
20047,1993-03-19,Sarah,Miller,F,2014-08-25
20048,1977-08-13,Michael,Lee,M,2009-01-19
20049,1986-04-26,Emma,Jackson,F,2012-11-06
20050,1963-08-07,Robert,Miller,M,2006-06-06
20051,1983-06-23,David,Williams,M,2010-09-06
20052,1997-11-12,Jessica,Miller,F,2015-06-01
20053,1975-02-25,William,Jackson,M,2011-01-24
20054,1989-08-04,Linda,Williams,F,2013-07-08
20055,1964-12-16,James,Miller,M,2008-02-10
20056,1971-05-03,Sarah,Williams,F,2009-06-29
20057,1980-01-11,Michael,Miller,M,2011-05-09
20058,1985-03-04,Emma,Jackson,F,2012-04-15
20059,1959-07-01,Robert,Williams,M,2006-01-09
20060,1990-01-02,David,Miller,M,2013-05-19
20061,1972-11-19,Jessica,Williams,F,2010-01-03
20062,1978-06-04,William,Miller,M,2009-03-09
20063,1984-05-15,Linda,Jackson,F,2012-02-01
20064,1967-09-02,James,Williams,M,2007-01-07
20065,1994-02-12,Sarah,Miller,F,2015-12-07
20066,1976-07-23,Michael,Jackson,M,2008-09-01
20067,1983-09-08,Emma,Williams,F,2011-08-01
20068,1962-02-14,Robert,Miller,M,2006-03-06
20069,1989-06-20,David,Jackson,M,2012-12-03
20070,1996-04-16,Jessica,Miller,F,2014-07-01
20071,1974-08-18,William,Williams,M,2010-06-01
20072,1987-11-06,Linda,Miller,F,2013-01-07
20073,1969-03-03,James,Jackson,M,2007-03-11
20074,1973-06-11,Sarah,Williams,F,2009-02-02
20075,1981-01-05,Michael,Miller,M,2011-09-01
20076,1986-02-10,Emma,Jackson,F,2012-07-01
20077,1960-08-02,Robert,Williams,M,2006-05-13
20078,1995-01-04,David,Miller,M,2014-02-03
20079,1977-05-19,Jessica,Jackson,F,2011-04-04
20080,1979-09-09,William,Williams,M,2009-05-04
20081,1985-07-03,Linda,Miller,F,2012-11-26
20082,1966-11-29,James,Williams,M,2007-02-05
20083,1975-04-06,Sarah,Miller,F,2009-01-05
20084,1983-09-13,Michael,Jackson,M,2011-06-01
20085,1987-06-02,Emma,Williams,F,2013-03-04
20086,1965-01-16,Robert,Miller,M,2006-09-04
20087,1992-12-29,David,Jackson,M,2014-05-01
20088,1970-09-01,Jessica,Williams,F,2010-08-09
20089,1978-02-12,William,Miller,M,2009-11-09
20090,1982-05-01,Linda,Jackson,F,2012-05-27
20091,1963-09-09,James,Williams,M,2007-01-01
20092,1979-03-03,Sarah,Miller,F,2009-08-03
20093,1988-07-29,Michael,Jackson,M,2012-09-03
20094,1985-04-10,Emma,Williams,F,2014-01-06
20095,1961-01-03,Robert,Miller,M,2006-07-03
20096,1997-06-23,David,Jackson,M,2015-11-02
20097,1972-12-07,Jessica,Williams,F,2010-05-01
20098,1976-06-16,William,Miller,M,2009-06-01
20099,1980-11-09,Linda,Jackson,F,2013-01-01
```