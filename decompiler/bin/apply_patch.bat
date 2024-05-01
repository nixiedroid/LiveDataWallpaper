set working_dir=%~dp0
set src_original_dir=app
set src_changed_dir=new
set patch_file=patch.txt
pushd %src_original_dir%
%working_dir%bin\patch -p1 -i %working_dir%%patch_file%
popd
pause