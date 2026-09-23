import glob
import os
import subprocess
import zipfile
import shutil

def build_26_3():
    mc_jar = r'C:\Users\kalas\AppData\Roaming\ModrinthApp\meta\versions\26.3-0.19.5\26.3-0.19.5.jar'
    libs = glob.glob(r'C:\Users\kalas\AppData\Roaming\ModrinthApp\meta\libraries\**\*.jar', recursive=True)
    p_mods = glob.glob(r'C:\Users\kalas\AppData\Roaming\ModrinthApp\profiles\2*\mods\*.jar')
    proc_mods = glob.glob(r'C:\Users\kalas\AppData\Roaming\ModrinthApp\profiles\2*\.fabric\processedMods\*.jar')

    # Exclude gpucraft jars from classpath to avoid collision
    p_mods = [m for m in p_mods if 'gpucraft' not in m.lower()]

    cp = [mc_jar] + libs + p_mods + proc_mods
    cp_str = ';'.join(cp)

    src_files = glob.glob(r'C:\Users\kalas\Desktop\GPUcraft\src\main\java\**\*.java', recursive=True)
    out_dir = r'C:\Users\kalas\Desktop\GPUcraft\build\classes_26.3'
    if os.path.exists(out_dir):
        shutil.rmtree(out_dir)
    os.makedirs(out_dir, exist_ok=True)

    argfile = r'C:/Users/kalas/Desktop/GPUcraft/build/javac_args.txt'
    with open(argfile, 'w', encoding='utf-8') as f:
        cp_escaped = cp_str.replace('\\', '/')
        out_escaped = out_dir.replace('\\', '/')
        f.write('-cp\n')
        f.write(f'"{cp_escaped}"\n')
        f.write('-d\n')
        f.write(f'"{out_escaped}"\n')
        f.write('--release\n')
        f.write('21\n')
        f.write('-encoding\n')
        f.write('UTF-8\n')
        for sf in src_files:
            sf_escaped = sf.replace('\\', '/')
            f.write(f'"{sf_escaped}"\n')

    print(f'Compiling {len(src_files)} Java files for Minecraft 26.3...')
    res = subprocess.run(['javac', f'@{argfile}'], capture_output=True, text=True)
    print('Javac STDOUT:', res.stdout)
    print('Javac STDERR:', res.stderr)
    if res.returncode != 0:
        print('Compilation FAILED!')
        return False
    print('Compilation SUCCEEDED!')

    # Package into JAR
    jar_out = r'C:\Users\kalas\Desktop\GPUcraft\build\libs\gpucraft-mc26.3-2.1.0.jar'
    os.makedirs(os.path.dirname(jar_out), exist_ok=True)

    with zipfile.ZipFile(jar_out, 'w', zipfile.ZIP_DEFLATED) as z:
        # Add compiled classes
        for root, _, files in os.walk(out_dir):
            for file in files:
                full_path = os.path.join(root, file)
                rel_path = os.path.relpath(full_path, out_dir).replace('\\', '/')
                z.write(full_path, rel_path)

        # Add resources
        res_dir = r'C:\Users\kalas\Desktop\GPUcraft\src\main\resources'
        for root, _, files in os.walk(res_dir):
            for file in files:
                full_path = os.path.join(root, file)
                rel_path = os.path.relpath(full_path, res_dir).replace('\\', '/')
                z.write(full_path, rel_path)

        # Add Manifest
        manifest_data = (
            "Manifest-Version: 1.0\r\n"
            "Fabric-Gradle-Version: 2.1.0\r\n"
            "\r\n"
        )
        z.writestr('META-INF/MANIFEST.MF', manifest_data)

    print(f'Successfully built: {jar_out}')

    # Copy to release folder and Modrinth test profile
    rel_folder = r'C:\Users\kalas\Desktop\GPUcraft_Release_v2.1.0'
    os.makedirs(rel_folder, exist_ok=True)
    shutil.copy2(jar_out, os.path.join(rel_folder, 'gpucraft-mc26.3-2.1.0.jar'))

    # Update Modrinth test profile
    profiles = glob.glob(r'C:\Users\kalas\AppData\Roaming\ModrinthApp\profiles\2*\mods')
    for p in profiles:
        for old_jar in glob.glob(os.path.join(p, 'gpucraft*.jar')):
            try:
                os.remove(old_jar)
                print(f'Removed old jar: {old_jar}')
            except Exception as e:
                print('Could not remove old jar:', e)
        target = os.path.join(p, 'gpucraft-mc26.3-2.1.0.jar')
        shutil.copy2(jar_out, target)
        print(f'Installed into test profile: {target}')

    return True

if __name__ == '__main__':
    build_26_3()
