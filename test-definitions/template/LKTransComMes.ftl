<cmpSqlResultsTest>
    <!-- diffTableSize="0"  nie generuje tabeli z wynikami (jest szybsze) -->
    <!--
    fetchSize       - number of records fetched at once. Default is 100.
    chunk           - number of differences after which further comparison is stopped. 0 (or if not set) means
                      never stop -> go to the end of all records. Number of differences visible in test result may
                      be smaller than real one if FETCH comparator stopped his job because of chunk. So if the number of
                      differences is equal to the chunk, it may mean that there are more differences. Default value is 0.
                      多少错误停下，默认0，不停止
    diffTableSize   - number of differences which will be displayed in logs. Even records from first DB/SQL and odd
                      from the second (so the table will have two times more rows than defined here). Default is 5.
    fileOutputOn    - boolean value - if true all results will be saved in CSV files (same name as XML test
                      configuration file but with different prefix and extension). File may be incomplete
                      (if chunk > 0 - see above).
                      In case of FETCH comparator 3 files will be generated:
                        test_name_diff.csv   - CSV with differences (all or chunked)
                        test_name_sql1.csv   - all (or chunked) data returned by first SQL (from config below)
                        test_name_sql2.csv   - all (or chunked) data returned by second SQL (from config below)
                      In case of MINUS comparator only 1 file will be generated:
                        test_name_minus.csv  - CSV with results returned by MINUS sql query
                      Default is false.
    delta           - when two compared values are floating-point, they will be considered as equal when difference
                      between them is less (or equal) than delta. You can set the same value as the BigDecimal/Java
                      can handle. Default is 0.
    -->
    <compare mode="FETCH" diffTableSize="50" fileOutputOn="true" >
        <sql datasourceName="uat"><![CDATA[
        select * from LKTransComMes a where a.contno = '${uat_contno}' 
        ]]></sql>
        <sql datasourceName="mptest"><![CDATA[
        select * from LKTransComMes a where a.contno = '${mptest_contno}'
        ]]></sql>
    </compare>
</cmpSqlResultsTest>