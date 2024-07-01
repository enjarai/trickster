let
  nixpkgsVer = "24.05";
  pkgs = import (fetchTarball "https://github.com/NixOS/nixpkgs/tarball/nixos-${nixpkgsVer}") { config = {}; overlays = []; };
  libs = with pkgs; [
    libpulseaudio
    libGL
    glfw
    openal
    stdenv.cc.cc.lib
  ];
in pkgs.mkShell {
  name = "trickster";

  buildInputs = with pkgs; [
    jdk21
  ] ++ libs;

  LD_LIBRARY_PATH = pkgs.lib.makeLibraryPath libs;
}
