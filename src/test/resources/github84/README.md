## NASA-PDS/mi-label#84 Testing

Unfortunately, we do not have an appropriate test framework setup at the time of this commit. So here is how you would test this using [NASA AMMOS Labelocity](https://github.com/NASA-AMMOS/labelocity):

```
$R2LIB/gen dummy.vic 10 10 ## really, any vicar file, it's not used but has to be present
java jpl.mipl.io.jConvertIIO inp=dummy.vic out=test_json.xml format=pds4 pds_detached_only=true pds_label_type=PDS4 velo_template=test_json.vm extra_file_name=test_json.json extra_file_type=json
```

