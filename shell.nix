{ pkgs ? import <nixpkgs> { } }:
let
  fhs = pkgs.buildFHSUserEnv {
    name = "gradle-env";
    targetPkgs = pkgs:
      (with pkgs; [
        gradle
        kotlin
        jdk
        zlib
        ncurses
        freetype
      ]);
  };
in pkgs.stdenv.mkDerivation {
  name = "antaeus";
  nativeBuildInputs = [ fhs ];

  shellHook = "exec gradle-env";
}
