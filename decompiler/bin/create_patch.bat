@echo off
set working_dir=%~dp0
set src_original_dir=app
set src_changed_dir=new
set patch_file=patch.txt


if not exist %src_original_dir% (
echo Folder with original files not found
echo Aborting
pause
goto :EOF
)
if not exist %src_changed_dir% (
echo Folder with changed files not found
echo Aborting
pause
goto :EOF
)


%working_dir%bin\diff --context=1 --strip-trailing-cr -r %src_original_dir% %src_changed_dir% > %patch_file%
